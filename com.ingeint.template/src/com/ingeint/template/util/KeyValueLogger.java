/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Copyright (C) 2019 INGEINT <https://www.ingeint.com> and contributors (see README.md file).
 */

package com.ingeint.template.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * This class abstracts the complexity of log management in key/value format,
 * used for monitoring applications. Use the builder pattern to build a log.
 * <p>
 * Example:
 * <p>
 * {@code KeyValueLogger keyValueLogger = KeyValueLogger.instance(App.class);}
 * <p>
 * Example using builder:
 * <p>
 * {@code keyValueLogger.message("Hello World!!").info();}
 * <p>
 * Log output:
 * <p>
 * {@code 08:07:26 [main] INFO App message="Hello World!!"}
 */
public class KeyValueLogger {

	private Logger logger;
	private List<Pair> pairs;
	private Throwable exception;

	private KeyValueLogger(Logger logger) {
		this.logger = logger;
		pairs = new ArrayList<>();
	}

	private KeyValueLogger(Logger logger, List<Pair> pairs, Throwable exception) {
		this.logger = logger;
		this.pairs = pairs;
		this.exception = exception;
	}

	/**
	 * Creates a object instance.
	 *
	 * @param origin Class
	 * @return Logger
	 * @see <a href=
	 *      "https://www.slf4j.org/api/org/slf4j/LoggerFactory.html#getLogger(java.lang.Class)"
	 *      target="_blank">LoggerFactory.getLogger</a>
	 */
	public static Logger logger(Class<?> origin) {
		return LoggerFactory.getLogger(origin);
	}

	/**
	 * Creates a new instance
	 * <a href="https://www.slf4j.org/manual.html" target="_blank">slf4j</a>.
	 *
	 * @param origin Class
	 * @return BUilder
	 * @see <a href=
	 *      "https://www.slf4j.org/api/org/slf4j/LoggerFactory.html#getLogger(java.lang.Class)"
	 *      target="_blank">LoggerFactory.getLogger</a>
	 */
	public static KeyValueLogger instance(Class<?> origin) {
		return new KeyValueLogger(LoggerFactory.getLogger(origin));
	}

	/**
	 * Creates a new instance
	 * <a href="https://www.slf4j.org/manual.html" target="_blank">slf4j</a>.
	 *
	 * @param logger Logger Example: {@code LoggerFactory.getLogger(App.class);}.
	 * @return Builder
	 * @see <a href=
	 *      "https://www.slf4j.org/api/org/slf4j/LoggerFactory.html#getLogger(java.lang.Class)"
	 *      target="_blank">LoggerFactory.getLogger</a>
	 */
	public static KeyValueLogger instance(Logger logger) {
		return new KeyValueLogger(logger);
	}

	/**
	 * Adds a new key value log
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.add("msg", "Hello World!!").info();}
	 * <p>
	 * Examaple output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App msg="Hello World!!"}
	 *
	 * @param key   Variable name
	 * @param value Variable value
	 * @return Builder
	 */
	public KeyValueLogger add(String key, Object value) {
		return add(key, null, value);
	}

	private KeyValueLogger add(KeyValueLoggerKeys key, Object value) {
		return add(key.toString(), value);
	}

	/**
	 * Adds a new key value log using a value string format.
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.add("message", "arg1 {}, arg2 {} y arg3 {}", 1, '2',
	 * "3").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 10:20:18 [main] INFO App message="arg1 1, arg2 2 y arg3 3"}
	 *
	 * @param key         Variable name
	 * @param valueFormat Value format
	 * @param values      Values to be format
	 * @return Builder
	 * @see <a href="https://www.slf4j.org/faq.html#logging_performance" target=
	 *      "_blank">Logging Performance</a>
	 * @see <a href=
	 *      "https://www.slf4j.org/apidocs/org/slf4j/helpers/MessageFormatter.html"
	 *      target="_blank">MessageFormat</a>
	 */
	public KeyValueLogger add(String key, String valueFormat, Object... values) {
		List<Pair> pairsCopy = new ArrayList<>(pairs);
		pairsCopy.add(new Pair(key, valueFormat, values));
		KeyValueLogger keyValueLogger = new KeyValueLogger(logger, pairsCopy, exception);
		return keyValueLogger;
	}

	private KeyValueLogger add(KeyValueLoggerKeys key, String valueFormat, Object... values) {
		return add(key.toString(), valueFormat, values);
	}

	/**
	 * Adds a new log using key "message".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.message("Hello World!!").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App message="Hello World!!"}
	 *
	 * @param value Value
	 * @return Builder
	 */
	public KeyValueLogger message(String value) {
		return add(KeyValueLoggerKeys.MESSAGE, value);
	}

	/**
	 * Agrega un mensaje al log con clave "message" using a format.
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.message("arg1 {}, arg2 {} y arg3 {}", 1, '2',
	 * "3").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 10:20:18 [main] INFO App message="arg1 1, arg2 2 y arg3 3"}
	 *
	 * @param format Format
	 * @param values Parameters
	 * @return Builder
	 * @see #add(String, String, Object...)
	 * @see <a href="https://www.slf4j.org/faq.html#logging_performance" target=
	 *      "_blank">Logging Performance</a>
	 * @see <a href=
	 *      "https://www.slf4j.org/apidocs/org/slf4j/helpers/MessageFormatter.html"
	 *      target="_blank">MessageFormat</a>
	 */
	public KeyValueLogger message(String format, Object... values) {
		return add(KeyValueLoggerKeys.MESSAGE, format, values);
	}

	/**
	 * Adds a new log using the key "exception".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.exception("Error!!!").error();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] ERROR App exception="Error!!!"}
	 *
	 * @param exception Exception as a String
	 * @return Builder
	 */
	public KeyValueLogger exception(String exception) {
		return add(KeyValueLoggerKeys.EXCEPTION, exception);
	}

	/**
	 * Adds a new log using the key "exception".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.exception(new RuntimeException("Error!!!")).error();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 10:20:18 [main] ERROR App exception="java.lang.RuntimeException: Error!!!"}
	 *
	 * @param exception Exception
	 * @return Builder
	 */
	public KeyValueLogger exception(Throwable exception) {
		return add(KeyValueLoggerKeys.EXCEPTION, exception);
	}

	/**
	 * Adds a new log using the key "exception" and prints the error stacktrace.
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.exceptionWithStackTrace("Custom Message", new RuntimeException("Error!!!")).error();}
	 * <p>
	 * Example output: {@code
	 * 10:20:18 [main] ERROR App exception="Custom Message"
	 * java.lang.RuntimeException: Error!!!
	 *     at App.main(App.java:24)
	 * }
	 *
	 * @param message   Custom Message
	 * @param exception Exception
	 * @return Builder
	 * @see <a href="https://www.slf4j.org/faq.html#paramException" target=
	 *      "_blank">Parameterize Exception</a>
	 */
	public KeyValueLogger exceptionWithStackTrace(String message, Throwable exception) {
		this.exception = exception;
		return add(KeyValueLoggerKeys.EXCEPTION, message);
	}

	/**
	 * Adds a new log using the key "exception" and prints the error stacktrace.
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.exceptionWithStackTrace(new RuntimeException("Error!!!")).error();}
	 * <p>
	 * Example output: {@code
	 * 10:20:18 [main] ERROR App exception="java.lang.RuntimeException: Error!!!"
	 * java.lang.RuntimeException: Error!!!
	 *     at App.main(App.java:24)
	 * }
	 *
	 * @param exception Exception
	 * @return Builder
	 * @see <a href="https://www.slf4j.org/faq.html#paramException" target=
	 *      "_blank">Parameterize Exception</a>
	 */
	public KeyValueLogger exceptionWithStackTrace(Throwable exception) {
		this.exception = exception;
		return add(KeyValueLoggerKeys.EXCEPTION, exception);
	}

	/**
	 * Adds a new log using the key "endpoint".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.endpoint("/info").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App endpoint="/info"}
	 *
	 * @param endpoint Value to log
	 * @return Builder
	 */
	public KeyValueLogger endpoint(String endpoint) {
		return add(KeyValueLoggerKeys.ENDPOINT, endpoint);
	}

	/**
	 * Adds a new log using the key "service".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.service("userService").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App service="userService"}
	 *
	 * @param service Value to log
	 * @return Builder
	 */
	public KeyValueLogger service(String service) {
		return add(KeyValueLoggerKeys.SERVICE, service);
	}

	/**
	 * Adds a new log using the key "name".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.name("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App name="value"}
	 *
	 * @param name Value to log
	 * @return Builder
	 */
	public KeyValueLogger name(String name) {
		return add(KeyValueLoggerKeys.NAME, name);
	}

	/**
	 * Adds a new log using the key "duration".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.duration(0.2).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App duration="0.2"}
	 *
	 * @param duration Value to log
	 * @return Builder
	 */
	public KeyValueLogger duration(double duration) {
		return add(KeyValueLoggerKeys.DURATION, duration);
	}

	/**
	 * Adds a new log using the key "duration".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.duration(10000).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App duration="10000"}
	 *
	 * @param duration Value to log
	 * @return Builder
	 */
	public KeyValueLogger duration(long duration) {
		return add(KeyValueLoggerKeys.DURATION, duration);
	}

	/**
	 * Adds a new log using the key "status".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.status("fail").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App status="fail"}
	 *
	 * @param status Value to log
	 * @return Builder
	 */
	public KeyValueLogger status(String status) {
		return add(KeyValueLoggerKeys.STATUS, status);
	}

	/**
	 * Adds a new log using the key "status" and value "fail".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.fail().info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App status="fail"}
	 *
	 * @return Builder
	 */
	public KeyValueLogger fail() {
		return add(KeyValueLoggerKeys.STATUS, KeyValueLoggerKeys.FAIL);
	}

	/**
	 * Adds a new log using the key "status" and value "success".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.success().info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App status="success"}
	 *
	 * @return Builder
	 */
	public KeyValueLogger success() {
		return add(KeyValueLoggerKeys.STATUS, KeyValueLoggerKeys.SUCCESS);
	}

	/**
	 * Adds a new log using the key "environment".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.environment("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App environment="value"}
	 *
	 * @param environment Value to log
	 * @return Builder
	 */
	public KeyValueLogger environment(String environment) {
		return add(KeyValueLoggerKeys.ENVIRONMENT, environment);
	}

	/**
	 * Adds a new log using the key "method".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.javaMethod("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App method="value"}
	 *
	 * @param javaMethod Value to log
	 * @return Builder
	 */
	public KeyValueLogger javaMethod(String javaMethod) {
		return add(KeyValueLoggerKeys.METHOD, javaMethod);
	}

	/**
	 * Adds a new log using the key "method".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.javaMethod(Dummy.class.getDeclaredMethod("toString")).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App method="toString"}
	 *
	 * @param javaMethod Value to log
	 * @return Builder
	 */
	public KeyValueLogger javaMethod(Method javaMethod) {
		return add(KeyValueLoggerKeys.METHOD, javaMethod == null ? null : javaMethod.getName());
	}

	/**
	 * Adds a new log using the key "class".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.javaClass("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App class="value"}
	 *
	 * @param javaClass Value to log
	 * @return Builder
	 */
	public KeyValueLogger javaClass(String javaClass) {
		return add(KeyValueLoggerKeys.CLASS, javaClass);
	}

	/**
	 * Adds a new log using the key "class".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.javaClass(Dummy.class).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App class="app.Dummy"}
	 *
	 * @param javaClass Value to log
	 * @return Builder
	 */
	public KeyValueLogger javaClass(Class<?> javaClass) {
		return add(KeyValueLoggerKeys.CLASS, javaClass == null ? null : javaClass.getName());
	}

	/**
	 * Adds a new log using the key "package".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.javaPackage("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App package="value"}
	 *
	 * @param javaPackage Value to log
	 * @return Builder
	 */
	public KeyValueLogger javaPackage(String javaPackage) {
		return add(KeyValueLoggerKeys.PACKAGE, javaPackage);
	}

	/**
	 * Adds a new log using the key "package".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.javaPackage(Dummy.class).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App package="app"}
	 *
	 * @param javaClass Value to log
	 * @return Builder
	 */
	public KeyValueLogger javaPackage(Class<?> javaClass) {
		return add(KeyValueLoggerKeys.PACKAGE, javaClass == null ? null : javaClass.getPackage().getName());
	}

	/**
	 * Adds a new log using the key "package".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.javaPackage(Dummy.class.getPackage()).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App package="app"}
	 *
	 * @param javaPackage Value to log
	 * @return Builder
	 */
	public KeyValueLogger javaPackage(Package javaPackage) {
		return add(KeyValueLoggerKeys.PACKAGE, javaPackage == null ? null : javaPackage.getName());
	}

	/**
	 * Adds a new log using the key "code".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.code("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App code="value"}
	 *
	 * @param code Value to log
	 * @return Builder
	 */
	public KeyValueLogger code(String code) {
		return add(KeyValueLoggerKeys.CODE, code);
	}

	/**
	 * Adds a new log using the key "track".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.track(UUID.randomUUID()).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App track="123e4567-e89b-42d3-a456-556642440000"}
	 *
	 * @param track Value to log
	 * @return Builder
	 */
	public KeyValueLogger track(UUID track) {
		return add(KeyValueLoggerKeys.TRACK, track);
	}

	/**
	 * Adds a new log using the key "track".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.track("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App track="value"}
	 *
	 * @param track Value to log
	 * @return Builder
	 */
	public KeyValueLogger track(String track) {
		return add(KeyValueLoggerKeys.TRACK, track);
	}

	/**
	 * Adds a new log using the key "request".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.request(UUID.randomUUID()).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App request="123e4567-e89b-42d3-a456-556642440000"}
	 *
	 * @param request Value to log
	 * @return Builder
	 */
	public KeyValueLogger request(UUID request) {
		return add(KeyValueLoggerKeys.REQUEST, request);
	}

	/**
	 * Adds a new log using the key "request".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.request("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App request="value"}
	 *
	 * @param request Value to log
	 * @return Builder
	 */
	public KeyValueLogger request(String request) {
		return add(KeyValueLoggerKeys.REQUEST, request);
	}

	/**
	 * Adds a new log using the key "session".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.session(UUID.randomUUID()).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App session="123e4567-e89b-42d3-a456-556642440000"}
	 *
	 * @param session Value to log
	 * @return Builder
	 */
	public KeyValueLogger session(UUID session) {
		return add(KeyValueLoggerKeys.SESSION, session);
	}

	/**
	 * Adds a new log using the key "session".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.session("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App session="value"}
	 *
	 * @param session Value to log
	 * @return Builder
	 */
	public KeyValueLogger session(String session) {
		return add(KeyValueLoggerKeys.SESSION, session);
	}

	/**
	 * Adds a new log using the key "id".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.id(UUID.randomUUID()).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App id="123e4567-e89b-42d3-a456-556642440000"}
	 *
	 * @param id Value to log
	 * @return Builder
	 */
	public KeyValueLogger id(UUID id) {
		return add(KeyValueLoggerKeys.ID, id);
	}

	/**
	 * Adds a new log using the key "id".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.id("3").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App id="3"}
	 *
	 * @param id Value to log
	 * @return Builder
	 */
	public KeyValueLogger id(String id) {
		return add(KeyValueLoggerKeys.ID, id);
	}

	/**
	 * Adds a new log using the key "type".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.type("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App type="value"}
	 *
	 * @param type Value to log
	 * @return Builder
	 */
	public KeyValueLogger type(String type) {
		return add(KeyValueLoggerKeys.TYPE, type);
	}

	/**
	 * Adds a new log using the key "value".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.value("value").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App value="value"}
	 *
	 * @param value Value to log
	 * @return Builder
	 */
	public KeyValueLogger value(String value) {
		return add(KeyValueLoggerKeys.VALUE, value);
	}

	/**
	 * Adds a new log using the key "transaction".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.transaction(UUID.randomUUID()).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App transaction="123e4567-e89b-42d3-a456-556642440000"}
	 *
	 * @param transaction Value to log
	 * @return Builder
	 */
	public KeyValueLogger transaction(UUID transaction) {
		return add(KeyValueLoggerKeys.TRANSACTION, transaction);
	}

	/**
	 * Adds a new log using the key "transaction".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.transaction("123e4567-e89b-42d3-a456-556642440000").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App transaction="123e4567-e89b-42d3-a456-556642440000"}
	 *
	 * @param transaction Value to log
	 * @return Builder
	 */
	public KeyValueLogger transaction(String transaction) {
		return add(KeyValueLoggerKeys.TRANSACTION, transaction);
	}

	/**
	 * Adds a new log using the key "httpMethod".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.httpMethod("GET").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App httpMethod="GET"}
	 *
	 * @param httpMethod Value to log
	 * @return Builder
	 */
	public KeyValueLogger httpMethod(String httpMethod) {
		return add(KeyValueLoggerKeys.HTTP_METHOD, httpMethod);
	}

	/**
	 * Adds a new log using the key "httpStatus".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.httpStatus("500").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App httpStatus="500"}
	 *
	 * @param httpStatus Value to log
	 * @return Builder
	 */
	public KeyValueLogger httpStatus(String httpStatus) {
		return add(KeyValueLoggerKeys.HTTP_STATUS, httpStatus);
	}

	/**
	 * Adds a new log using the key "httpStatus".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.httpStatus(500).info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App httpStatus="500"}
	 *
	 * @param httpStatus Value to log
	 * @return Builder
	 */
	public KeyValueLogger httpStatus(int httpStatus) {
		return add(KeyValueLoggerKeys.HTTP_STATUS, httpStatus);
	}

	/**
	 * Adds a new log using the key "language".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.language("es").info();}
	 * <p>
	 * Example output:
	 * <p>
	 * {@code 08:07:26 [main] INFO App language="es"}
	 *
	 * @param language Value to log
	 * @return Builder
	 */
	public KeyValueLogger language(String language) {
		return add(KeyValueLoggerKeys.LANGUAGE, language);
	}

	/**
	 * Adds log with key "arguments".
	 * <p>
	 * Example:
	 * <p>
	 * {@code keyValueLogger.arguments(new Object[]{"1", "2"}).info();}
	 * <p>
	 * Output example:
	 * <p>
	 * {@code 08:07:26 [main] INFO App arguments="[1, 2]"}
	 *
	 * @param arguments Values
	 * @return Builder
	 */
	public KeyValueLogger arguments(Object[] arguments) {
		return add(KeyValueLoggerKeys.ARGUMENTS, arguments);
	}

	/**
	 * Logs level INFO.
	 */
	public void info() {
		logger.info(createStringFormat(), createArgumentsList());
	}

	/**
	 * Logs level DEBUG.
	 */
	public void debug() {
		logger.debug(createStringFormat(), createArgumentsList());
	}

	/**
	 * Logs level ERROR.
	 */
	public void error() {
		logger.error(createStringFormat(), createArgumentsList());
	}

	/**
	 * Logs level TRACE.
	 */
	public void trace() {
		logger.trace(createStringFormat(), createArgumentsList());
	}

	/**
	 * Logs level WARN.
	 */
	public void warn() {
		logger.warn(createStringFormat(), createArgumentsList());
	}

	private String createStringFormat() {
		return pairs.stream().filter(Pair::isValid).map(Pair::getKeyFormat).collect(joining(" "));
	}

	private Object[] createArgumentsList() {
		List<Object> arguments = pairs.stream().filter(Pair::isValid).flatMap(pair -> pair.getStringValues().stream())
				.collect(toList());

		if (exception != null) {
			arguments.add(exception);
		}

		return arguments.toArray();
	}

	private class Pair {
		private static final String NULL = "null";
		private static final String KEY_VALUE_FORMAT = "%s=\"%s\"";
		private static final String DEFAULT_CUSTOM_VALUE_FORMAT = "{}";

		private String key;
		private Object[] values;
		private String valueFormat;

		Pair(String key, String valueFormat, Object[] values) {
			this.key = key == null ? NULL : key;
			this.valueFormat = valueFormat == null ? DEFAULT_CUSTOM_VALUE_FORMAT : valueFormat;
			this.values = values == null ? new Object[] {} : values;
		}

		boolean isValid() {
			return !key.isEmpty();
		}

		String getKeyFormat() {
			String cleanKey = key.replaceAll("[^a-zA-Z0-9_.]", "");
			return String.format(KEY_VALUE_FORMAT, cleanKey, valueFormat);
		}

		List<String> getStringValues() {
			return Arrays.stream(values).map(this::valueToString).map(this::cleanValue).collect(toList());
		}

		private String valueToString(Object value) {
			if (value == null) {
				return NULL;
			}

			if (value instanceof Object[]) {
				return Arrays.toString((Object[]) value);
			} else if (value instanceof int[]) {
				return Arrays.toString((int[]) value);
			} else if (value instanceof double[]) {
				return Arrays.toString((double[]) value);
			} else if (value instanceof long[]) {
				return Arrays.toString((long[]) value);
			} else if (value instanceof boolean[]) {
				return Arrays.toString((boolean[]) value);
			} else if (value instanceof byte[]) {
				return Arrays.toString((byte[]) value);
			} else if (value instanceof short[]) {
				return Arrays.toString((short[]) value);
			} else if (value instanceof float[]) {
				return Arrays.toString((float[]) value);
			} else if (value instanceof char[]) {
				return Arrays.toString((char[]) value);
			}

			return value.toString();
		}

		private String cleanValue(String value) {
			return value.replace("'", "").replace("\"", "").replace("\n", " ").trim();
		}
	}

	private enum KeyValueLoggerKeys {
		PACKAGE("package"), CLASS("class"), ENDPOINT("endpoint"), SERVICE("service"), EXCEPTION("exception"),
		HTTP_STATUS("httpStatus"), HTTP_METHOD("httpMethod"), TRANSACTION("transaction"), VALUE("value"), TYPE("type"),
		SESSION("session"), TRACK("track"), REQUEST("request"), CODE("code"), METHOD("method"),
		ENVIRONMENT("environment"), STATUS("status"), MESSAGE("message"), NAME("name"), DURATION("duration"),
		LANGUAGE("language"), ARGUMENTS("arguments"), ID("id"), FAIL("fail"), SUCCESS("success");

		private final String toStringKey;

		KeyValueLoggerKeys(String toStringKey) {
			this.toStringKey = toStringKey;
		}

		@Override
		public String toString() {
			return toStringKey;
		}
	}

}
