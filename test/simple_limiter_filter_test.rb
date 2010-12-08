require 'rubygems'
require 'java'
gem 'minitest' # ensures you're using the gem, and not the built in MT
require 'minitest/autorun'
require 'minitest/spec'
require 'minitest/pride'
require File.join(File.dirname(__FILE__), "/../dist/SimpleLimiterFilter.jar")

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
  
  before do

  end
  
  it "should pass rate limiting" do
    ipstats = ADS::IPStats.new(10, 60000, 300000)
    ipstats.shouldRateLimit("1.1.1.1", get_time).must_equal false
  end

  it "should fail rate limiting with more that 10 requests in 60 seconds" do
    ipstats = ADS::IPStats.new(10, 60000, 300000)
    t = get_time
    (1..15).each {|i| ipstats.shouldRateLimit("1.1.1.1", t)}
    ipstats.shouldRateLimit("1.1.1.1", get_time).must_equal true
  end
end

describe "IPTracker" do
  include JavaHelpers
  
  describe "new" do
    before do
      @ipTracker = ADS::IPTracker.new("2.2.2.2", get_time)
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
      @ipTracker = ADS::IPTracker.new("2.2.2.2", (get_time - (75*1000)))
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
end
