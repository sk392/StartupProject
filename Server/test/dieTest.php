<!DOCTYPE html>
<html>
<head>
	<title>JavaScript View</title>
</head>
<body>
	<input type="button" value="Say hello" onClick="showAndroidToast('Hello Android!')" />

	<script type="text/javascript">
		function showAndroidToast(toast) {
			android.showToast(toast);
		}
	</script>
</body>
</html>