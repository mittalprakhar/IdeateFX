# IdeateFX
An interactive JavaFX application that accelerates the process of coming up with startup ideas, and helps quantify and sort them on the basis of quantitative metrics.

<img src="https://github.com/mittalprakhar/IdeateFX/blob/main/demo.jpg?raw=true" alt="Demo" width="600"/>

### Features
- Questions to help brainstorm startup ideas
- Input validation for all form fields
- Edit and delete functionality for existing ideas
- Observable list to display ideas, sorted by feasibility metrics
- Ability to read from and save ideas to a local file
- Event listener to remind user to store unsaved changes before closing app

### Instructions
```
> javac --module-path javafx-sdk-11.0.2/lib --add-modules=javafx.controls --add-modules=javafx.media IdeateFX.java
> java --module-path javafx-sdk-11.0.2/lib --add-modules=javafx.controls --add-modules=javafx.media IdeateFX
```