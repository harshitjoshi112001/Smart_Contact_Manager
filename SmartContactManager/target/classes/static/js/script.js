console.log("This is script file");

const toggleSidebar = () => {
	if($(".sidebar").is(":visible"))
	{
		//we have to close sidebar
		console.log("close");
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}
	else
	{
		//we have to open sidebar
		console.log("open");
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};

const search = () => {
	//console.log("searching..");
	let query = $("#search-input").val();   //id of input search
	//console.log(query);
	if(query == ""){
		$(".search-result").hide();
	} else {
		//search
		//console.log(query);
		
		//sending request to server
		let url = `http://localhost:8080/search/${query}`;
		
		fetch(url).then((response) => {
			
			return response.json();
			
		}).then((data) => {
			
			//console.log(data);
			
			let text = `<div class='list-group'>`;
			
			data.forEach((contacts) => {
				text += `<a href='/user/${contacts.cid}/contact' class='list-group-item list-group-item-action'>${contacts.name}</a>`
			});
			
			text += `</div>`;
		$(".search-result").html(text);			
		$(".search-result").show();
		});
		
	}
};
