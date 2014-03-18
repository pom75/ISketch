package {	import flash.display.Sprite;	import flash.display.MovieClip;	import flash.net.XMLSocket;	import flash.display.MovieClip;	import fl.motion.Color;	import flash.events.IOErrorEvent;	import flash.events.Event;	import flash.events.DataEvent;	import flash.events.MouseEvent;	import flash.events.IOErrorEvent;	import flash.events.SecurityErrorEvent;	import flash.events.UncaughtErrorEvents;	public class ConnexionSocket	{		//Variable permettant de savoir si le client est connecté		private var bConnected:Boolean;		//Pour cibler vers la scène accecible uniquement dans le paktage		internal var scene:MovieClip=InterfaceSock.scene;		//Instance de la connection		private var connexion:XMLSocket = new XMLSocket();		// Id du client		private var idClient:Number;
		// port du serveur
		private var port:Number;
		//ip du serveur
		private var host:String;		//Constructeur :		public function ConnexionSocket(host:String,port:Number)		{
			this.host = host;
			this.port = port;
			
			
			try{
				connexion.connect(host,port);
			}catch(error:Error){
				scene.gotoAndStop(1);
			}
						connexion.addEventListener(SecurityErrorEvent.SECURITY_ERROR, connexionN2);			//Connexion non etablie			connexion.addEventListener(IOErrorEvent.IO_ERROR, connexionN1);			//CONECTION AU SOCKET REUSSI			connexion.addEventListener(Event.CONNECT, connexionR);			//Conection au socket interrompu;			connexion.addEventListener(Event.CLOSE, connexionC);			//Reception de donné;			connexion.addEventListener(DataEvent.DATA, receiveData);			//On établie la conection au server
			
					}
		
		public function getCo():XMLSocket		{			return connexion;		}
		
				//Affcihe sur le client lorsque la conexion est établie		public function connexionR(Evt:Event):void		{			//scene.bug.text="connexion Reussi";		}		//Affiche sur le client lorsque la conexion est perdu		public function connexionC(Evt:Event):void		{
				//scene.bug.text="connexion couper";				scene.gotoAndStop(1);
				new MessageBox(scene,"Le serveur a été coupé","=(");				//scene.accueil.setError("Le Serveur vien d'etre couper , veuillez pacienter quelques minute avant de vous reconnecté");				scene.mainFenetre.effacerThis();				scene.accueil.afficherThis();		}				public function connexionN1(e:IOErrorEvent):void		{
								scene.gotoAndStop(1);
				new MessageBox(scene,"Impossbile de se connecter au serveur","=(");				//scene.accueil.setError("Serveur Off-Line");				scene.mainFenetre.effacerThis();				scene.accueil.afficherThis();		}				public function connexionN2(e:SecurityErrorEvent):void		{
								scene.gotoAndStop(1);
				new MessageBox(scene,"Impossbile de se connecter au serveur","=(");				//scene.accueil.setError("Serveur Off-Line");				scene.mainFenetre.effacerThis();				scene.accueil.afficherThis();		}		//Traitement de l'information lors de reçois d'un message du serveur		public function receiveData(Evt:DataEvent)		{
			var s:String = Evt.data;
			s = s.replace("\n","");
			//scene.traceBug.text +=  "reçus : "+s+"\n";			var tabData:Array = s.split("/");			if (tabData[0] == "CONNECTED")			{
				//CONNECTED/user/
				InterfaceSock.clientCo(tabData[1]);			}			else if (tabData[0] == "EXITED")			{
				//EXITED/user/
				InterfaceSock.clientDeco(tabData[1]);			}
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
			}		}
		
				//converti le message a envoyer en XML et l'envois		public function sendText(sText:String)		{
			//scene.traceBug.text +=  "envoyé : "+sText+"\n";
			var xText:XML = new XML(sText);
			connexion.send(xText+ "\n");		}	}}