# example of ruby file that uses ant
# requires jruby 1.5
require 'ant'

namespace :ivy do
  ivy_install_version = '2.2.0'
  ivy_jar_dir = './ivy'
  ivy_jar_file = "#{ivy_jar_dir}/ivy.jar"

  task :download do
    mkdir_p ivy_jar_dir
    ant.get :src => "http://repo1.maven.org/maven2/org/apache/ivy/ivy/#{ivy_install_version}/ivy-#{ivy_install_version}.jar",
      :dest => ivy_jar_file,
      :usetimestamp => true
  end
  
  desc "install ivy"
  task :install => :download do
    ant.path :id => 'ivy.lib.path' do
      fileset :dir => ivy_jar_dir, :includes => '*.jar'
    end

    ant.taskdef :resource => "org/apache/ivy/ant/antlib.xml",
      #:uri => "antlib:org.apache.ivy.ant",
      :classpathref => "ivy.lib.path"
  end
end

build_dir = "build"
project_name = "SimpleLimiterFilter"

desc "resolve dependencies"
task :resolve => "ivy:install" do
  ant.retrieve(:organisation => "tomcat",
               :module => "servlet-api",
               :revision => "5.5.23",
               :pattern => 'lib/[conf]/[artifact].[ext]',
               :inline => "true")
end

task :props => :setup do
  ant.echo :message => ">>> ${line.separator}"
  ant.property :name => "props", :refid => "project.class.path"
  ant.echo :message => ">>> ${props}"
end

task :setup => :resolve do
  ant.property :name => "src.dir", :value => "src"
  ant.property :name => "dist.dir", :value => "#{build_dir}/dist"
  ant.mkdir :dir => "#{build_dir}/classes"
  ant.mkdir :dir => "${dist.dir}"
  ant.path(:id => "project.class.path") do
    pathelement :location => "#{build_dir}/classes"
    fileset :dir => "lib"
  end
end

task :compile => :setup do
  ant.javac(:destdir => "#{build_dir}/classes") do
    classpath :refid => "project.class.path"
    src { pathelement :location => "${src.dir}" }
  end
end

task :jar => :compile do
  ant.jar :destfile => "${dist.dir}/#{project_name}.jar", :basedir => "#{build_dir}/classes"
end

task :clean do
  ant.delete :dir => build_dir
end

task :test => :jar do
  Dir["test/**/*.rb"].each do |f|
    puts "running: #{f}"
    load f
  end

end

task :default => :jar
