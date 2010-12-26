require 'rubygems'
require 'java'

gem 'minitest' # ensures you're using the gem, and not the built in MT
require 'minitest/autorun'
require 'minitest/spec'
require 'minitest/pride'

Dir["#{File.dirname(__FILE__)}/../build/**/*.jar"].each do |f|
  require f
end

module ADS
  include_package "edu.duke.ads"
end

module JavaHelpers
  def get_time
    java.util.Date.new.getTime
  end
end

describe "IPStats" do
  include JavaHelpers
  
  it "should pass rate limiting" do
    ipstats = ADS::IPStats.new(10, 60000, 300000)
    ipstats.shouldRateLimit("1.1.1.1", get_time).must_equal false
  end

  it "should fail rate limiting with more that 10 requests in 60 seconds" do
    ipstats = ADS::IPStats.new(10, 60000, 300000)
    t = get_time
    (1..10).each {|i| ipstats.shouldRateLimit("1.1.1.1", t)}
    ipstats.shouldRateLimit("1.1.1.1", get_time).must_equal true
  end

  describe "cleaning IPs" do

    before do
      @visits = 10
      @ipstats = ADS::IPStats.new(10, 5000, 300000)
      t = get_time - 10000
      (1..@visits).each {|i| @ipstats.shouldRateLimit("1.1.1.1", t)}
    end

    it "should have all visits if clean isn't called" do
      ip_tracker = @ipstats.getIPTracker("1.1.1.1")
      ip_tracker.size.must_equal @visits
    end
    
    it "should remove IP that was not recently visited" do
      @ipstats.clean
      ip_tracker = @ipstats.getIPTracker("1.1.1.1")
      ip_tracker.must_equal nil
    end

  end
  
end

describe "IPTracker" do
  include JavaHelpers
  
  describe "new" do
    before do
      @ipTracker = ADS::IPTracker.new("2.2.2.2", get_time, 60000)
    end
    
    it "should have a single entry" do
      @ipTracker.size.must_equal 1
    end
    
    it "should have an ipAddress of 2.2.2.2" do
      @ipTracker.getIpAddress.must_equal "2.2.2.2"
    end

    it "should have a size of 2 when another entry is added" do
      @ipTracker.addEntry(30, (30*1000), get_time)
      @ipTracker.size.must_equal 2
    end
  end

  describe "with older entries" do
    before do
      @ipTracker = ADS::IPTracker.new("2.2.2.2", (get_time - (75*1000)), 60000)
    end

    it "should remove initial entry and leave a size of 1" do
      @ipTracker.addEntry(30, (30*1000), get_time)
      @ipTracker.size.must_equal 1
    end
    
    (10..14).each do |i|
      it "should leave a size of #{i} when #{i} are added" do
        i.times do |v|
          @ipTracker.addEntry(30, (30*1000), get_time)
        end
        @ipTracker.size.must_equal i
      end
    end

    describe "of 3" do
      it "should remove 4 old entries and leave a size of 1" do
        requests = 30
        timePeriod = 30 * 1000
        (1..3).each {|i| @ipTracker.addEntry(requests, timePeriod, (get_time - (60*1000)))}
        @ipTracker.size.must_equal 4 # could entries
        @ipTracker.addEntry(requests, timePeriod, get_time)
        @ipTracker.size.must_equal 1
      end
    end
  end

  describe "with banded entries" do
    before do
      @time = get_time
      @reqs = 2
      @band_time = 5000
      @ipTracker = ADS::IPTracker.new("2.2.2.2", @time, @band_time)
      @ipTracker.addEntry(@reqs, @band_time, @time)
    end

    it "should haved reach limit" do
      @ipTracker.hasReachedRateLimit(@reqs, @band_time, get_time).must_equal true
    end

    it "should rate limit with less than 5 seconds" do
      @ipTracker.hasReachedRateLimit(@reqs, @band_time, (@time+4999)).must_equal true
    end
    
    it "should not rate limit with greater than 5 seconds" do
      @ipTracker.hasReachedRateLimit(@reqs, @band_time, (@time+5001)).must_equal false
    end
  end
end
