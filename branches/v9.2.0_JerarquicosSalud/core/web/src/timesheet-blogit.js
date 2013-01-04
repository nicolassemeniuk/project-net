// Js for creating the inline blogit and evaluation

// Create Inline blog it window
function createInlineBlogit(rowCount, objectID){
	var tc = document.getElementById('timecard');
	var newRowPosition = tc.rows.length;
	var nextRowPosition;
	var rowId = document.getElementById(rowCount+'_row');
	
	for(var i=0; i< tc.rows.length; i++){
		if(tc.rows[i].id == rowId.id) {
			newRowPosition = i + 1;
			nextRowPosition = newRowPosition + 1;
			break;
		}
	}
	var row = tc.insertRow(newRowPosition);
	row.id = 'pbr1'+objectID;
	row.className = 'pb';
	
	var ttlCells = tc.rows[1].cells;
	for(var i = 0; i< ttlCells.length; i++) {
		var nextCell = row.insertCell(i);
		if(i == 0) {
			nextCell.className = 'lhac';
			nextCell.innerHTML = haMsg;
		} else if(i == 1){
			nextCell.className = 'fhac';
			nextCell.id = 'hA0X';
			nextCell.innerHTML = '&nbsp';
		} else {
			nextCell.className = 'ihac';
			nextCell.id = 'hA'+(i-1)+'X';
			nextCell.innerHTML = '&nbsp';
		}
	}
	
	var row2 = tc.insertRow(nextRowPosition);
	row2.className = 'pb';
	row2.id = 'pbr2'+objectID;
	var cell1 = row2.insertCell(0);
	cell1.className = 'shac';
	
	var el1 = document.createElement('span');
	el1.className = 'bl';
	el1.innerHTML = blMsg + '<br/>';
	
	var el2 = document.createElement('span');
	el2.className = 're';
	el2.innerHTML = msgFormat.format(reBlogMessage, document.getElementById('a'+objectID).innerHTML.trim());
	
	var _div = document.createElement('div');
	_div.className = 'wd';
	_div.innerHTML = doneMsg + '<input type="checkbox" id="wD"/>';
	
	cell1.appendChild(el1);
	cell1.appendChild(el2);
	cell1.appendChild(_div);
	
	var cell2 = row2.insertCell(1);
	cell2.className  = 'bsmc';
	cell2.colSpan = colSpanCount + 1;
	cell2.noWrap = 'nowrap';
	
	var iTbl = document.createElement('table');
	iTbl.className  = 'bsmt';

	// create text box for subject	
	var ro = iTbl.insertRow(0);
	var cl = ro.insertCell(0);
	cl.width = '92%';
	
	var subjectInput = document.createElement('input');
	subjectInput.type = 'text';
	subjectInput.className = 'bs';
	subjectInput.id = 'bS';
	subjectInput.onblur = new Function('ssm()');
	subjectInput.onfocus = new Function('ss()');
	
	cl.appendChild(subjectInput);
	
	// Create text area for blog message
	var ro1 = iTbl.insertRow(1);
	var cl1 = ro1.insertCell(0);
	
	var messageText = document.createElement('textarea');
	messageText.cols = '64';
	messageText.id = 'bM';
	messageText.className = 'bm';
	messageText.onblur = new Function('ssm()');
	messageText.onfocus = new Function('sm()');
	cl1.appendChild(messageText);

	var cl2 = ro1.insertCell(1);
	
	var el3 = document.createElement('span');
	el3.className = 'sl';
	
	var saTag = document.createElement('a');
	saTag.className = 'ol';
	saTag.style.color = '#3399FF';
	saTag.onclick = new Function('pbm()');
	saTag.innerHTML = submitLink;
	el3.appendChild(saTag);
	
	var el4 = document.createElement('hr');
	el4.className = 's';
	
	var el5 = document.createElement('span');
	el5.className = 'sl';
	
	var raTag = document.createElement('a');
	raTag.className = 'ol';
	raTag.onclick = new Function('rC()');
	raTag.innerHTML = resetLink;
	raTag.style.color = '#3399FF';
	el5.appendChild(raTag);
	
	cl2.appendChild(el3);
	cl2.appendChild(el4);
	cl2.appendChild(el5);
	cell2.appendChild(iTbl);
	blogitOpen = true;
	setLengthAndValue('bS', 'bM');
}

// Function for removing the inline blogit row
function removeInlineBlogit(browCount) {
	var tc = document.getElementById('timecard');
	tc.deleteRow(browCount);
	tc.deleteRow(browCount);
	blogitOpen = false;
}

// Set default values and max length for the subject text.
function setLengthAndValue(sid, mid) {
	document.getElementById(sid).setAttribute('value', blogSubject);
	document.getElementById(mid).setAttribute('value', blogMessage);
	document.getElementById(sid).setAttribute('maxLength', 240);
}