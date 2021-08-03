package com.hansum.migration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;

@MapperScan(basePackageClasses =  MigrationApplication.class)
@SpringBootApplication
public class MigrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MigrationApplication.class, args);

		System.out.println("WEB 시작됨");

	}
}
