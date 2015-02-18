<?php

$dbstr = "(DESCRIPTION= (ADDRESS_LIST=
(ADDRESS= (PROTOCOL=TCP)
(HOST=db12.cse.cuhk.edu.hk)
(PORT=1521) )
) (CONNECT_DATA=
(SERVER=DEDICATED)
(SERVICE_NAME=db12.cse.cuhk.edu.hk) )
)";

$conn = oci_connect("d093", "123456", $dbstr);

if (!$conn) {
	echo "ERROR: cannot establish the connection\n";
}

/* Initiate _POST variables */
function init($var, $defaultVal){
	if (!isset($_POST[$var])) {
		$_POST[$var] = $defaultVal;
	}
}

init("bpp", "10");
init("prevbpp", "10");
init("curpage", "1");
init("prevcurpage", "1");

if ($_POST["prevbpp"] != $_POST["bpp"] && $_POST["prevcurpage"] == $_POST["curpage"]) {
	$_POST["curpage"] = 1;
}
$prevbpp = $_POST["bpp"];
$prevcurpage = $_POST["curpage"];

function sqlPrepare ($operation, $searchField) {
	if ($operation == "bookListContent") {
		return "SELECT b1.callnum AS callnum, b1.title AS title, a.aname AS aname, c.copynum AS copynum, nvl(tmp.checkedoutcopy, 0) AS checkedoutcopy FROM authorship a, copy c, book b1 LEFT JOIN (SELECT b2.callnum, COUNT(*) AS checkedoutcopy FROM book b1, borrow b2 WHERE b1.callnum = b2.callnum AND return IS NULL GROUP BY b2.callnum) tmp ON b1.callnum = tmp.callnum WHERE b1.callnum = a.callnum AND b1.callnum = c.callnum " . $searchField;
	}
}

$sql = "";
$setOperator = ($_POST["all"]?"INTERSECT ":"UNION ");
$haveConstrain = false;

if ($_POST["callnum"]) {
	$callnum = "AND b1.callnum = '" . $_POST["callnum"] . "' ";
	$sql .= ($haveConstrain?$setOperator:"") . sqlPrepare("bookListContent", $callnum);
	$haveConstrain = true;
}
		
if ($_POST["title"]) {
	$title = "AND b1.title LIKE '%" . $_POST["title"] . "%' ";
	$sql .= ($haveConstrain?$setOperator:"") . sqlPrepare("bookListContent", $title);
	$haveConstrain = true;
}

if ($_POST["author"]) {
	$author = "AND a.aname LIKE '%" . $_POST["author"] . "%' ";
	$sql .= ($haveConstrain?$setOperator:"") . sqlPrepare("bookListContent", $author);
	$haveConstrain = true;
}

if ($_POST["keyword"]) {
	$keyword = "AND b1.callnum || b1.title || a.aname LIKE '%" . $_POST["keyword"] . "%' ";
	$sql .= ($haveConstrain?$setOperator:"") . sqlPrepare("bookListContent", $keyword);
	$haveConstrain = true;
}
		
if (!$haveConstrain) {
	$sql = sqlPrepare("bookListContent", " ");
}
		
$sql .= "ORDER BY callnum, title, aname, copynum asc ";
	
$stid = oci_parse($conn, $sql);
oci_execute($stid);
$i = 0;
while ($row[$i] = oci_fetch_object($stid)) {
	$i++;
}
for ($j = 0, $rowcount = 1, $output = ''; $j < $i; $j++, $rowcount++) {
	$outputLine = '';
	$outputLine .= '<tr>';
	$outputLine .= '<td width="120">' . $row[$j]->CALLNUM . '</td>';
	$outputLine .= '<td>' . $row[$j]->TITLE . '</td>';
	$outputLine .= '<td width="140">' . $row[$j]->ANAME;
	while ($row[$j]->CALLNUM == $row[$j+1]->CALLNUM) {
		$j++;
		if ($row[$j]->ANAME != $row[$j-1]->ANAME) {
			$outputLine .= '<br>' . $row[$j]->ANAME;
		}
	} 
	$outputLine .= '</td>';
	$outputLine .= '<td width="120" style="text-align:center">' . $row[$j]->COPYNUM . '</td>';
	$outputLine .= '<td width="140" style="text-align:center">' . $row[$j]->CHECKEDOUTCOPY . '</td>';
	$outputLine .= '</tr>';
	if ($rowcount > $_POST["bpp"]*($_POST["curpage"]-1) && $rowcount <= $_POST["bpp"]*$_POST["curpage"]) { // Print only for the rows in range
		$output .= $outputLine;
	}
}

?>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Library Inquiry System</title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<form action="" method="POST">
<div id="main">
	<div id="content">
		<div id="searchBar">
			<table align="center">	
				<tr>
					<td><br>Call Number</td>
					<td><br>Title</td>
					<td><br>Author</td>
					<td><br>Keyword</td>
					<td>Satisfy all<br>search keys?</td>
					<td></td>
				</tr>
				<tr>
					<td><input type="input" id="callnum" name="callnum" size="12" maxlength="8" value="<?php echo $_POST["callnum"]; ?>"></td>
					<td><input type="input" id="title" name="title" size="12" maxlength="30" value="<?php echo $_POST["title"]; ?>"></td>
					<td><input type="input" id="author"  name="author" size="12" maxlength="25" value="<?php echo $_POST["author"]; ?>"></td>
					<td><input type="input" id="keyword"  name="keyword" size="12" maxlength="30" value="<?php echo $_POST["keyword"]; ?>"></td>
					<td><input type="checkbox" id="all" name="all" value="yes" <?php echo ($_POST["all"]?'checked':''); ?>></td>
					<td><input type="submit" id="search" name="search" value="Search"></td>
				</tr>
			</table>
		</div>
		<br>
		<div id="BookCount">No. of Books Selected: 
        <?php
		echo $rowcount-1;
        ?>
        </div>
		<div id="bookList">
			<table id="bookList" width="100%">
				<tr>
					<th width="120"><br>Call Number</th>
					<th><br>Title</th>
					<th width="140" style="text-align:center"><br>Author(s)</th>
					<th width="120" style="text-align:center">No. of<br>Book Copies</th>
					<th width="140" style="text-align:center">No. of Checked-Out<br>Book Copies</th>

				</tr>
			</table>
		</div>
		<div id="BookListContent">
        <?php
		echo '<table id="bookListContent" width="100%">';
		echo $output;
		echo '</table>';
		?>
        </div>
		<div id="bpp">
			No. of Books Per Page: 
			<select name="bpp" id="bpp" onChange="submit(this.form)">
            <?php
			for ($k = 5; $k <= 15; $k += 5) {
				echo '<option value="' . $k . '"';
				if ($k == $_POST["bpp"]) {
					echo ' selected';
				}
				echo '>' . $k . '</option>';
			}
			?>
			</select>
		</div>
		<br>
		<div id="paginationBar">
        <?php
		if ($rowcount-1 > $_POST["bpp"]) {
        	echo 'Current Page:';
			echo '<select name="curpage" id="curpage" onChange="submit(this.form)">';
			for ($k = 1; $k <= ceil(($rowcount-1)/$_POST["bpp"]); $k++) {
				echo '<option value="' . $k . '"';
				if ($k == $_POST["curpage"]) {
					echo ' selected';
				}
				echo '>' . $k . '</option>';
			}
			echo '</select>';
		}
		?>
		</div>
	</div>
</div>
<input type="hidden" name="prevbpp" value="<?php echo $prevbpp; ?>">
<input type="hidden" name="prevcurpage" value="<?php echo $prevcurpage; ?>">
</form>
</body>
</html>
<?php
oci_close($conn);
?>