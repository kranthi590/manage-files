(function() {
	$.getJSON("/list-files", {}).done(function(json) {
		const data = [];
		if(json.data) {
			Object.keys(json.data).forEach(function(key){
				var childNodesArray = json.data[key];
				var object = {
						text: key,
						nodes: [],
						child: false
				}
				childNodesArray.forEach(function(childNode){
					var arr = childNode.split('/');
					object.nodes.push({
						text: "<a>" + arr[arr.length-1] + "</a>",
						fullName: childNode,
						child: true,
						name: arr[arr.length-1]
					});
				});
				data.push(object);
			});
		}
		
		
		$('#tree').treeview({
			data,
		});
		$('#tree').on('nodeSelected', function(event, data) {
			if(data.child) {
				$.ajax({
					  url: "/downloadPdf",
					  xhrFields: {
					    responseType: 'blob'
					  },
					  data: {path: data.fullName},
					  success: function(blob){
					    console.log(blob.size);
					      var link=document.createElement('a');
					      link.href=window.URL.createObjectURL(blob);
					      link.download=data.name;
					      link.click();
					  }
					});
			}
		});
		
	}).fail(function(jqxhr, textStatus, error) {
		var err = textStatus + ", " + error;
		console.log("Request Failed: " + err);
	});
})();