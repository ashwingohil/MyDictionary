# MyDictionary - A command based tool
Personal Dictionary to keep track of learned words along with the novel names where they occured. 


Running the source: java MyDictionary.java
In-built user is "mojo". Password - "nopassword"

>## Outline of the project:
1) A user registers with the program and logs in.

2) A user pushes a dictionary word info as learnt from sources like novel(or any book). Such word information includes word, meaning, derivatives and Novel(Book) info where the word is learnt. The word information is stored in files in a user's folder and is computed for different features. 
  For now, features are **write word info** and **search for word**. **System fetches root word also if derivative is searched.**
  **Register, Login, Forgot Password works.**
  
  The working model is using naked files to store password and authenticates using md5 hash for passwords. In future such passwords can be stored in database.

3) **Ideation:** **The concept of each user designated as a Java object. Such objects stay afloat in the system even if user logs off.** Such objects store all files info into variables when constructor is run. **So search operation does not look for in the files once info is loaded if the user logs back in.** Use of Lists, hash authentication, file operations.

>## Future Scope:
Add info to a word, edit word info file , delete word . And Search novel names for words learnt.


>## Screenshots:
1) **Greetings page**

![image](https://user-images.githubusercontent.com/26901597/205211400-8b36a9cb-7734-4bb0-aa2a-c1047e195fc9.png)

**2) Register (password input is shadowed)**

![image](https://user-images.githubusercontent.com/26901597/205211679-929a6e48-6bd7-44fb-8e41-43699ece0cbd.png)

**3) Login**

![image](https://user-images.githubusercontent.com/26901597/205211841-613816e5-0528-4191-bae7-a03b2bd4ae67.png)

**4) Input Word and/or derivates**

![image](https://user-images.githubusercontent.com/26901597/205216327-ce1b2efe-de9d-4194-b997-321727f8b51b.png)

**5) Search for word or derivative (For example searching by derivative - trod)**

![image](https://user-images.githubusercontent.com/26901597/205216590-5a61f3c3-d624-4f9f-8403-a8e1d6021bcb.png)

**6) Input all information like novel(book) information**

![image](https://user-images.githubusercontent.com/26901597/205217239-2165ab53-ccfb-43c3-b962-8208f82250fe.png)

**7) Getting complete info on a word search if all info is provided (search word - communicate)**

![image](https://user-images.githubusercontent.com/26901597/205217461-fb9e0813-9530-43e8-aa20-ad0e4e79eb16.png)










