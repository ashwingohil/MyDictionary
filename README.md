# MyDictionary
Personal Dictionary to keep track of learned words along with the novel names where they occured. 


Running the source: java MyDictionary.java
In-built user is "mojo". Password - "nopassword"

Outline of the project:
1) A user registers with the program and logs in.

2) A user pushes a dictionary word info as learnt from sources like novel(or any book). Such word information includes word, meaning, derivatives and Novel(Book) info where the word is learnt. The word information is stored in files in a user's folder and information is computed for different features. 
  For now, features are write word info and search for word. System fetches root word if derivative is searched.
  Register, Login, Forgot Password works.
  The working model is using naked files to store password and authenticates using md5 hash for passwords. In future such passwords   
  can be stored in database.

3) Ideation: The concept of each user designated as a Java object. Such objects stay afloat in the system even if users log off. Such objects store all files info into variables when constructor is run. So search operation does not look for in the files once info is loaded. Use of Lists, hash authentication, file operations.

Future Scope:
Add info to a word, edit word info file , delete word . And Search novel names for words learnt.
