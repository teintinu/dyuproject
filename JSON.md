# Pojo serialization/deserialization #

Here's a sample pojo:

```

public class Person
{
    
    private String firstName, lastName, email, username, password;
    private int age;
    
    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    public String getLastName()
    {
        return lastName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public int getAge()
    {
        return age;
    }
    public void setAge(int age)
    {
        this.age = age;
    }
    
    

}

```

Let's subclass Person with Employee

```

public class Employee extends Person
{
    
    private List<Task> tasks;

    public List<Task> getTasks()
    {
        return tasks;
    }

    public void setTasks(List<Task> tasks)
    {
        this.tasks = tasks;
    }

}

```

Here's a sample config:

`employee.json`
```

{
  "employee":
  {
    "class": "com.dyuproject.json.test.Employee",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john_doe1@email.com",
    "age": 21,
    "username": "john_doe1",
    "password": "some_password1",
    "tasks":
    [
      {
        "class": "com.dyuproject.json.test.Task",
        "name": "Some task 1",
        "status": "10"
      },
      {
        "class": "com.dyuproject.json.test.Task",
        "name": "Some task 1",
        "status": "15"
      },
      {
        "class": "com.dyuproject.json.test.Task",
        "name": "Some task 1",
        "status": "20"
      }
    ]
  }
}

```


Let us test:
```
    public void testParse() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/json/test/employee.json";
        StandardJSON json = new StandardJSON();
        File file = new File(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        Map<String,Object> map = (Map<String,Object>)json.parse(new ReaderSource(reader));
        Employee e = (Employee)map.get("employee");
        assertTrue(e!=null);
        assertTrue("John".equals(e.getFirstName()));
        List<Task> tasks = e.getTasks();
        assertTrue(tasks!=null && tasks.size()==3);
        assertTrue("10".equals(tasks.get(0).getStatus()));
        assertTrue("15".equals(tasks.get(1).getStatus()));
        assertTrue("20".equals(tasks.get(2).getStatus()));
    }

```

# JSON IOC #

One advantage of using `ApplicationContext` is that you can import configuration files and reference pojos from it.

Using the pojo Person.java from above, here's an example:

`basic.json`
```

{
  "person":
  {
    "class": "com.dyuproject.ioc.test.Person",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john_doe@email.com",
    "age": 20,
    "username": "john_doe",
    "password": "some_password"
  }
}

```

`import_basic.json`
```

{
  "import":
  {
    "class": "com.dyuproject.ioc.config.Import",
    "resources": 
    [
      "classpath:com/dyuproject/ioc/test/basic.json"
    ]  
  },
  "person1":
  {
    "class": "com.dyuproject.ioc.test.Person",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john_doe1@email.com",
    "age": 21,
    "username": "john_doe1",
    "password": "some_password1"
  }
}

```

Let us test:
```
    public void testBasic() throws Exception
    {        
        String resource = "src/test/resources/com/dyuproject/ioc/test/basic.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        Person person = (Person)ac.findPojo("person");
        assertTrue("John".equals(person.getFirstName()));
        assertTrue(20==person.getAge());
    }
    
    public void testImportBasic() throws Exception
    {
        String resource = "src/test/resources/com/dyuproject/ioc/test/import_basic.json";
        ApplicationContext ac = ApplicationContext.load(resource);
        assertTrue(ac.getPojo("person")==null);
        Person person = (Person)ac.findPojo("person");
        assertTrue("John".equals(person.getFirstName()));
        assertTrue(20==person.getAge());
        
        Person person1 = (Person)ac.getPojo("person1");
        assertTrue("John".equals(person1.getFirstName()));
        assertTrue(21==person1.getAge());
    }
```