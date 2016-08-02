# Js.on
Js.on is designed to bring the joy of *Javascript Object* semantics to *Java* without the usual integration pain, type-conversion overhead or API-clunkyness.

Imagine code stunts like ...


**Js.on Java-Object wrapping**
	
	Object o = new Object() {
		String identifier = "ufo";
		Point2D[] sightings = {
				new Point2D.Double(100,200),
				new Point2D.Double(200,300)
			};
			
		public String toString() 
		{ return Arrays.toString(sightings); }
	};
	
	Js j = Js.on( o );
	j.get("sightings").get(1).put("x", 250);
	j.stringify().equals( "{identifier:\"ufo\",sightings:[{x:100,y:200},{x:250,y:300}]}" );
	
	o.toString.equals("[{100,200},{250,300}]");


**Js.as Java Type coercion**

	// register.json
	[ { "name": "John Appleseed", "age": 35, "employer": { "name": "Apple inc.", "capitalization": 500e9 } ]
	
	interface Employee {
		String name();
		int age();
		
		Company employer();
		interface Company {
			String name();
			@Js.key("capitilization") long netWorth();
		}
	}
	
	Employee[] es = Js.from(registerReader).as(Employee[].class);


**Js.on().as() type-safe duck-typing** (all compatible calls like "getX" transparently mapped from VectorF to Point2D) 

	wargame.launchMissile( Js.on( new Point2D.Double(1,2) ).as(Vector2f.class) );

... and much more

	Js.on(1,2,3,4,5).concat( Js.on("x","y","z"), false ).slice(4, 9).join(":")
	.equals("5:x:y:z");
	
	Arrays.toString( Js.on( 1,2,3).put(9, false).to(Object[].class) )
	.equals("[1, 2, 3, undefined, undefined, undefined, undefined, undefined, undefined, false]");
	
	Js.on("10.0").equals(10) == true
	Js.on(false).equals("0") == true
	
	Js.on(1,2,3,4).get(100).equals(false) == true
	

Release
-------

The current state of the project is to be considered "incomplete and in development".

Releases are deployed automatically to the deploy branch of this github repostory. 
To add a dependency to *Js.on* using maven, modify your *repositories* section to include the git based repository.

	<repositories>
	 ...
	  <repository>
	    <id>Js.on-Repository</id>
	    <name>Js.on Git-based repo</name>
	    <url>https://raw.githubusercontent.com/Holzschneider/Js.on/deploy/</url>
	  </repository>
	...
	</repositories>
	
and modify your *dependencies* section to include the dependency
 
	  <dependencies>
	  ...
	  	<dependency>
	  		<groupId>de.dualuse.commons</groupId>
	  		<artifactId>Js.on</artifactId>
	  		<version>LATEST</version>
	  	</dependency>
	  ...
	  </dependencies>



To add the repository and the dependency using gradle refer to this

	repositories {
	    maven {
	        url "https://raw.githubusercontent.com/Holzschneider/Js.on/deploy/"
	    }
	}

and this

	dependencies {
	  compile 'de.dualuse.commons:Js.on:0.+'
	}
