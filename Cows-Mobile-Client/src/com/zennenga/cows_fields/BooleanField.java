package com.zennenga.cows_fields;

public class BooleanField extends BaseField{
	
	public BooleanField (String fieldName, String data)	{
		this.fieldName = fieldName;
		this.data = data;
		this.optional = true;
	}
	@Override
	public void setData(String newData) {
		this.beenValidated = false;
		if (newData != "false" && newData != "true") throw new IllegalArgumentException("Boolean fields must have a boolean value");
		this.data = newData;
		this.beenValidated = true;
	}
}