package  {
	import flash.display.MovieClip;
	import flash.utils.*;
	import flash.external.ExternalInterface;
	
	
	
	public class InterfaceSock extends MovieClip {
		public static var scene:MovieClip;
		public static var pseudo:String;
		public static var x1:int;
		public static var y1:int;
		public static var xB:int;
		public static var yB:int;
		
		public function InterfaceSock(s:MovieClip) {
			scene=s;
		}
		
		
		
		//Fonctions  client -> Serveur
		public static function connexionGuest(pseudoo:String)
		{
			scene.connexion.sendText("CONNECT/" + pseudoo);
		}
		public static function deconnexionGuest()
		{
			scene.connexion.sendText("EXIT/" + pseudo);
			scene.mainFenetre.effacerThis();
			scene.gotoAndPlay(1);
		}
		public static function motClient(mot:String)
		{
			scene.connexion.sendText("GUESS/" + mot);
		}
		public static function changeCouleur(hex:Number)
		{
			scene.connexion.sendText("SET_COLOR/" + ((hex & 0xFF0000) >> 16) +"/"+ ((hex & 0x00FF00) >> 8) +"/"+ (hex & 0x0000FF));
		}
		public static function tailleTrait(s:int)
		{
			scene.connexion.sendText("SET_SIZE/" + s);
		}
		public static function traceTrait(x2:int,y2:int)
		{
			//scene.connexion.sendText("SET_LINE/" + x2 +"/"+ y2 +"/"+ x2 +"/"+ y2);
			if(x1 != -1 && y1 != -1){
				xB = x2;
				yB = y2;
				scene.connexion.sendText("SET_LINE/" + x1 +"/"+ y1 +"/"+ x2 +"/"+ y2);
				x1 = -1 ;
				y1 = -1 ;
			}else{
				x1= x2;
				y1 = y2;
				scene.connexion.sendText("SET_LINE/" + xB +"/"+ yB +"/"+ x2 +"/"+ y2);
			}
		}
		
		public static function report(){
			if(scene.mainFenetre.info.getStart()){
				scene.connexion.sendText("CHEAT/" + scene.mainFenetre.info.getCurrentD());
			}
		}
		
		public static function pass(){
			scene.connexion.sendText("PASS/");
		}
		
		public static function inscription(user:String,pass:String){
			scene.connexion.sendText("REGISTER/" + user +"/"+ pass );
		}
		
		public static function coInscrit(user:String,pass:String){
			scene.connexion.sendText("LOGIN/" + user +"/"+ pass );
		}
		
		public static function spectateur(){
			scene.connexion.sendText("SPECTATOR");
			pseudo = "spec";
			
		}
		
		public static function iRCClient(text:String)
		{
			scene.connexion.sendText("TALK/" + text);
		}
		
		public static function clearD()
		{
			scene.connexion.sendText("CLEAR/");
		}
		
		
		
		
		
		
		//Fonction Serveur->Client
		
		public static function clientCo(user:String){
			scene.mainFenetre.addPre(user);
		}
		public static function welcome(user:String){
			if(pseudo != "spec"){
				pseudo=user;
				scene.gotoAndPlay(2);
				scene.mainFenetre.addNew(pseudo);
				scene.mainFenetre.addPre(pseudo);
			}else{
				scene.gotoAndPlay(2);
				scene.mainFenetre.addNew(pseudo);
				scene.mainFenetre.currentUser.setSpec(true);
			}
		}
		
		public static function clientDeco(user:String){
			scene.fenetre.removeClient(user);
			
		}
		public static function role(user:String,mot:String){
			scene.mainFenetre.reponse.addT("***** Nouveau round !");
			//On lance le timer
			scene.mainFenetre.info.startT();
			scene.mainFenetre.dessin.deleteStageE();
			//Client est un chercheur
			if(user == "chercheur"){
				scene.mainFenetre.dessin.isDessinateur(false);//On désactive les interaction avec les dessins s'il était dessinateur
				scene.mainFenetre.info.setCurrentD(mot); //On indique au client qui dessine
				scene.mainFenetre.info.cacheMot(); //Si le joueur était dessinateur on cache son mot
				scene.mainFenetre.currentUser.setNDes(); //Si le joueur était dessinateur , on change son status
			}
			//Client est un dessinateur
			else{
				new MessageBox(scene ,"A vous de dessiner !!","Information");
				scene.mainFenetre.dessin.isDessinateur(true);//On active les interaction de dessins
				scene.mainFenetre.currentUser.setDes(); //On passe le status du joueur en dessinateur
				scene.mainFenetre.info.setCurrentD(pseudo); //On indique au client qui est le dessinateur(lui)
				scene.mainFenetre.info.setMot(mot); //On indique au client quel moi il doit faire deviner
			}
		}
		public static function reponseC(mot:String,user:String){
			scene.mainFenetre.reponse.addT(mot+ " : " + user);
		}
		public static function trouveC(user:String){
			scene.mainFenetre.reponse.addT("***** " + user +" a trouvé le mot !");
		}
		public static function enclancheT(time:String){
			scene.mainFenetre.info.changeT(0,uint(time));
		}
		public static function scoreC(user:String,score:String){
			for(var i:int; i< scene.mainFenetre.lesCo.pseudo.length ; i++){
				if(scene.mainFenetre.lesCo.pseudo[i].getPseudo() == user ){
					scene.mainFenetre.lesCo.pseudo[i].setPoints(int(score));
					break;
				}
			}
			scene.mainFenetre.lesCo.refrechP();
			
		}
		public static function finiR(user:String,mot:String){
			scene.mainFenetre.reponse.addT("***** Fin du round !");
			scene.mainFenetre.info.stopT();
			scene.mainFenetre.info.setMot(mot);
			scene.mainFenetre.info.pass.visible=false;
			scene.mainFenetre.info.vainqueur(user);
		}
		
		static function sleep(ms:int):void {
			var init:int = getTimer();
			while(true) {
				if(getTimer() - init >= ms) {
					break;
				}
			}
		}
		
		static function drawL(x1:int,y1:int,x2:int,y2:int,red:int,green:int,blue:int,s:int){
			var intVal:uint = red << 16 | green << 8 | blue;
			var hexVal = '0x'+ intVal.toString(16);
			scene.mainFenetre.dessin.changeSeting(s,hexVal);
			scene.mainFenetre.dessin.dessinExt(x1,y1,x2,y2);
		}
		
		

		static function log(message:String):void{
			trace (message);
			if (ExternalInterface.available){
				ExternalInterface.call('console.log', message);
			}
		}
		
		public static function iRCRecu(user:String,text:String){
			scene.mainFenetre.iRC.addT(user +" : "+ text);
		}
		
		public static function clearedD(){
			scene.mainFenetre.dessin.deleteStageE();
		}
		public static function coImp(){
			scene.gotoAndPlay(1);
			new MessageBox(scene,"Impossible de se connecté au serveur pour l'instant","=(");
		}
		public static function full(){
			scene.gotoAndPlay(1);
			new MessageBox(scene,"Le serveur est actuelement plein","=(");
		}

	}
	
}
