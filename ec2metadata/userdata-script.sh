#!/bin/bash
# Update the package index
sudo yum update -y

# Install OpenJDK 21
sudo amazon-linux-extras enable corretto8
sudo yum install -y java-21-amazon-corretto-devel
sudo dnf install mariadb105 # install mysql connector, to connect to rds db instance from ec2

# Remove any existing HTTP servers (like Apache or Nginx) if they exist
sudo yum remove -y httpd
sudo yum remove -y nginx

# Install AWS CLI if not already installed
sudo yum install -y awscli

# Install crontab
sudo yum install -y cronie
sudo systemctl start crond
sudo systemctl enable crond

# Create a directory for your application
mkdir -p /home/ec2-user/app
sudo chown ec2-user:ec2-user /home/ec2-user/app

# Navigate to the application directory
cd /home/ec2-user/app

# Download your Spring Boot application JAR from S3
aws s3 cp s3://java-app-returning-region-and-az/ec2metadata-0.0.1-SNAPSHOT.jar .

# Run the Spring Boot application
java -jar ec2metadata-0.0.1-SNAPSHOT.jar > /home/ec2-user/app/application.log 2>&1 &

# Ensure the application starts on reboot
(crontab -l 2>/dev/null; echo "@reboot java -jar /home/ec2-user/app/ec2metadata-0.0.1-SNAPSHOT.jar > /home/ec2-user/app/application.log 2>&1 &") | crontab -

# mysql -h ${host} -P ${port} -u ${user_name} -p