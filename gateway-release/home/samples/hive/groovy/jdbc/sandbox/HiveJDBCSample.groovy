/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.sql.DriverManager

user = "guest";
password = user + "-password";
gatewayHost = "localhost";
gatewayPort = 8443;
contextPath = "gateway/sandbox/hive";
connectionString = String.format( "jdbc:hive2://%s:%d/?hive.server2.transport.mode=https;hive.server2.thrift.http.path=%s", gatewayHost, gatewayPort, contextPath );

// Load Hive JDBC Driver
Class.forName( "org.apache.hive.jdbc.HiveDriver" );

// Configure JDBC connection
connection = DriverManager.getConnection( connectionString, user, password );

statement = connection.createStatement();

// Disable Hive authorization - This can be ommited if Hive authorization is configured properly
statement.execute( "set hive.security.authorization.enabled=false" );

// Drop sample table to ensure repeatability
statement.execute( "DROP TABLE logs" );

// Create sample table
statement.execute( "CREATE TABLE logs(column1 string, column2 string, column3 string, column4 string, column5 string, column6 string, column7 string) ROW FORMAT DELIMITED FIELDS TERMINATED BY ' '" );

// Load data into Hive from file /tmp/log.txt which is placed on the local file system
statement.execute( "LOAD DATA LOCAL INPATH '/tmp/sample.log' OVERWRITE INTO TABLE logs" );

resultSet = statement.executeQuery( "SELECT * FROM logs" );

while ( resultSet.next() ) {
  System.out.println( resultSet.getString( 1 ) + " --- " + resultSet.getString( 2 ) );
}

resultSet.close();
statement.close();
connection.close();
