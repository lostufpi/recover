package enums;

public enum KindVariabilityEnum {
	
	MODIFIER("Modifier"),
	METHOD("Method"),
	ATTRIBUTE("Attribute"),
	PACKAGE("Package"),
	RETURN_TYPE("Return Type"), 
	THROWN_EXCEPTION("Thrown Exception"),
	CLASS("Class"),
	EQUALS("Equals"),
	BODY_OF_METHOD("Body of Method");
	
	private String name;
	
	private KindVariabilityEnum(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

}
