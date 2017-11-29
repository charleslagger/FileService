<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View content file</title>
</head>
<body>
	<h1>File Content</h1>

	<div>
		<table>
			<thead>
				<tr>
					<th>File Name</th>
					<th>Type</th>
					<th width="300"></th>
					<th width="100"></th>
				</tr>
			</thead>
			<tbody>
					<tr>
						<td>${fileName}</td>
						<td>${type}</td>
						<td><a
							href="<c:url value='/download-file/${fileName}/${partnerId}'/>"
							class="btn btn-success custom-width">Download</a></td>
						<td><a
							href="<c:url value='/view-file/${fileName}/${partnerId}'/>"
							class="btn btn-danger custom-width">View</a></td>
							
					</tr>

			</tbody>
		</table>
	</div>

</body>
</html>