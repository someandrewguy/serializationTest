package com.reflectchans;

public class TestValueJava implements java.io.Serializable {
	private static final long serialVersionUID = -4629892868661516528L;
	private String name;
	private int id;
	private TestValueJavaType type;

	enum TestValueJavaType {
		ZERO, ONE, TWO;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TestValueJavaType getType() {
		return type;
	}

	public void setType(TestValueJavaType type) {
		this.type = type;
	}
}
