<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<html>
<head>
<title>Upload a File</title>
</head>
<body>
	<form method="POST" action="uploadFile" enctype="multipart/form-data">
		Choose file: <input type="file" name = "file"><br>
		Name: <input type="text" name = "name"><br>
		
		<input type="submit" value="Submit">
	</form>
</body>
</html>
