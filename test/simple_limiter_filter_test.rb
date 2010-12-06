require 'rubygems'
require 'java'
gem 'minitest' # ensures you're using the gem, and not the built in MT
require 'minitest/autorun'
require 'minitest/spec'
require File.join(File.dirname(__FILE__), "/../dist/SimpleLimiterFilter.jar")

module ADS
  include_package "edu.duke.ads"
end

describe "IPStats" do
  it "should have something" do
    ipstats = ADS::IPStats.new(10, 60000)
    ipstats.shouldRateLimit("1.1.1.1", 239293923).must_equal false
  end
end
