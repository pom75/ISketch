package  {
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.events.KeyboardEvent;
	import flash.events.Event;
	/*Class Fenetre
		Class Contener qui contient toute les classes graphic et qui gere l'affichage principal des sous fenetres
	*/
	public class Fenetre extends MovieClip{
		public var scene:MovieClip=InterfaceSock.scene;  
		public var iRC:IRC; //Fenetre de Dialogues
		public var xIRC:int = 0;
		public var yIRC:int = 600;
		public var reponse:Reponse; //Fenetre des Réponses
		public var xRep:int = 750;
		public var yRep:int = 400;
		public var lesCo:LesCo; // Fenetre des connecté
		public var xCo:int = 750;
		public var yCo:int = 0;
		public var info:InfoPartie; 
		public var xInfo:int = 500;
		public var yInfo:int = 600;
		public var dessin:Dessin;
		public var xDess:int = 0;
		public var yDess:int = 0;
		public var options:Options;
		public var xOpt:int = 980;
		public var yOpt:int = 0;
		public var messBox:MessageBox;
		public var currentUser:User;
		public var buffClient:User;
		public var tabClients:Array = new Array(); //Tableau des Clients
		
		
		public function Fenetre() {
			instanceFenetre();
		}
		
		//Cree LE client 
		public function addNew(pseudo:String):void{
			this.currentUser = new User(pseudo);
		}
		
		
		//Cree un client extérieur
		public function addPre(pseudo:String):void{
			this. buffClient = new User(pseudo);
			tabClients.push(buffClient);
			this.lesCo.addC(buffClient); // ajoute le pseudo dans la fenetre des connecté
		}
		
		
		//Retir et suprime un joueur graphiquement et dans la méméoire
		public function removeClient(pseudo:String){
			var i:int = cherchePseudo(pseudo);
			this.lesCo.suppP(tabClients[i].pseudo); // retir le pseudo dans la fenetre des connecté
			tabClients[i]=null;
			tabClients.splice(i,1);
		}
		
		public function instanceFenetre():void{
			this.afficherThis();
			this.instanceDessin();
			this.instanceDialBoxs();
			this.instanceLesCo();
			this.instanceInfoP();
			this.instanceOptions();
		}
		
		//Instanciation et affichage de la fenetre des connecté
		public function instanceLesCo(){
			lesCo = new LesCo(this,xCo,yCo);
			lesCo.afficherThis();
		}
		
		//Instanciation et affichage de la fenetre des connecté
		public function instanceDessin(){
			dessin = new Dessin(this,xDess,yDess);
			dessin.afficherThis();
		}
		
		//Instanciation et affichage de la fenetre des connecté
		public function instanceInfoP(){
			info = new InfoPartie(this,xInfo,yInfo);
			info.afficherThis();
		}
		
		//Instanciation et affichage de la fenetre des pptions
		public function instanceOptions(){
			options = new Options(this,xOpt,yOpt);
			options.afficherThis();
		}
		
		//Instanciation et affichage de la fenetre de dialogue
		public function instanceDialBoxs(){
			iRC = new IRC(this,xIRC,yIRC);
			iRC.afficherThis();
			
			reponse = new Reponse(this,xRep,yRep);
			reponse.afficherThis();
		}
		
		//Affichage de la Fenetre
		public function afficherThis(){
			scene.addChild(this);
		}
		
		//Effacement de la Fenetre 
		public function effacerThis(){
			scene.removeChild(this);
			scene.mainFenetre=null;
		}
		
		
		public function cherchePseudo(pseudo:String){
			for(var i:int=0;i<tabClients.length;i++){
					if(tabClients[i].getPseudo() == pseudo){
						return i;
					}
			}
		}

	}
	
}