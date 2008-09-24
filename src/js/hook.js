var alreadyHooked = false;
for(var i = 0; i < Campfire.Responders.length; i++) {
  if(Campfire.Responders[i] == "Hook") {
    alreadyHooked = true;
    break;
  }
}

if(!alreadyHooked) {
  
  Campfire.Hook = Class.create({
    initialize: function(chat) {
      this.chat = chat;
    },
    
    onMessagesInserted: function(messages) {
      for (var i = 0; i < messages.length; i++) {
        var message = messages[i];
        if (message.kind == "text") {
          var msg = message.bodyElement().innerHTML.unescapeHTML();
          var id = message.id();
          var author = message.author();
          alert("CampfireMessage!@@@!"+id+"!@@@!"+author+"!@@@!"+msg);
        }
      }
    }
  });
  
  Campfire.Responders.push("Hook");
  chat.register.apply(chat, Campfire.Responders);
}
