<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>UploadFile</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link href="<c:url value='/static/css/bootstrap.css' />"
	rel="stylesheet" type="text/css"></link>
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"
	type="text/css"></link>
</head>
<body>
	<div align="center">
		<form:form method="POST" modelAttribute="multiFileBucket"
			action="upload" enctype="multipart/form-data" class="form-horizontal">
			<h1 class="title">Upload</h1>
			<div>
				<table border="0" cellpadding="5" class="table table-fit">
					<tr>
						<th class="label-color">File Name</th>
						<th></th>
						<th class="label-color">Mandatory</th>
					</tr>
					<c:forEach items="${multiFileBucket.files}" var="v" varStatus="vs">
						<tr>
							<td class="text-color"><c:out value="${doctypeField[vs.index]}" /></td>
							<td><form:input type="file" path="files[${vs.index}].file"
									id="files[${vs.index}].file" class="form-control input-sm" />
								<c:if test="${requiredField[vs.index] == '1'}">
									<div class="has-error">
										<form:errors path="files[${vs.index}].file"
											class="help-inline" />
									</div>
								</c:if></td>

							<td class="text-color"><c:if
									test="${requiredField[vs.index] == '1'}">
									<c:out value="(*)" />
								</c:if></td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<br>
			<input type="submit" value="OK" class="btn btn-primary btn-sm" />
		</form:form>
	</div>
</body>
</html>