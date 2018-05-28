package org.nio.use.api;

import java.io.Serializable;

public class HelloServiceImpl implements HelloService, Serializable {

	private static final long serialVersionUID = -6041418234834886928L;

	@Override
	public String hello(String name) {
		return "hello " + name;
	}

}
