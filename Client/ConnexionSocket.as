﻿package 
		// port du serveur
		private var port:Number;
		//ip du serveur
		private var host:String;
			this.host = host;
			this.port = port;
			
			
			try{
				connexion.connect(host,port);
			}catch(error:Error){
				scene.gotoAndStop(1);
			}
			
			
			
		

		
		
				//scene.bug.text="connexion couper";
				new MessageBox(scene,"Le serveur a été coupé","=(");
				
				new MessageBox(scene,"Impossbile de se connecter au serveur","=(");
				
				new MessageBox(scene,"Impossbile de se connecter au serveur","=(");
			var s:String = Evt.data;
			s = s.replace("\n","");
			//scene.traceBug.text +=  "reçus : "+s+"\n";
				//CONNECTED/user/
				InterfaceSock.clientCo(tabData[1]);
				//EXITED/user/
				InterfaceSock.clientDeco(tabData[1]);
			else if (tabData[0] == "NEW_ROUND")
			{
				//NEW_ROUND/user/mot/
					InterfaceSock.role(tabData[1],tabData[2]);
				
			}
			else if (tabData[0] == "GUESSED")
			{
				//GUESSED/mot/user/
				InterfaceSock.reponseC(tabData[1],tabData[2]);
			}
			else if (tabData[0] == "WELCOME")
			{
				//WELCOME/user/
				InterfaceSock.welcome(tabData[1]);
			}
			else if (tabData[0] == "WORD_FOUND")
			{
				//WORD_FOUND/joueur/
				InterfaceSock.trouveC(tabData[1]);
			}
			else if (tabData[0] == "WORD_FOUND_TIMEOUT")
			{
				//WORD_FOUND_TIMEOUT/timeout/
				InterfaceSock.enclancheT(tabData[1]);
			}
			else if (tabData[0] == "END_ROUND")
			{
				//END_ROUND/user/mot/
				InterfaceSock.finiR(tabData[1],tabData[2]);
			}
			else if (tabData[0] == "SCORE_ROUND")
			{
				for(var i:int = 1;i < tabData.length; i=i+2){
					InterfaceSock.scoreC(tabData[i],tabData[(i+1)]);
				}				
				//SCORE_ROUND/user1/score1/users2/score2/.../userN/scoreN/
			}
			else if (tabData[0] == "LINE")
			{
				//LINE/x1/y1/x2/y2/r/g/b/s/
				InterfaceSock.drawL(tabData[1],tabData[2],tabData[3],tabData[4],tabData[5],tabData[6],tabData[7],tabData[8]);
			}
			else if (tabData[0] == "LISTEN")
			{
				//LISTEN/joueur/texte/
				InterfaceSock.iRCRecu(tabData[1],tabData[2]);
			}
			
			else if (tabData[0] == "CLEARED")
			{
				
				InterfaceSock.clearedD();
			}
			else if (tabData[0] == "ACCESSDENIED")
			{
				
				InterfaceSock.coImp();
			}
			else if (tabData[0] == "GAME_FULL")
			{
				
				InterfaceSock.full();
			}
		
		
			//scene.traceBug.text +=  "envoyé : "+sText+"\n";
			var xText:XML = new XML(sText);
			connexion.send(xText+ "\n");