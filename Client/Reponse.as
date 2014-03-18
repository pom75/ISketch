package  {
	import flash.display.MovieClip;
	import flash.text.TextField;
	import flash.events.MouseEvent;
	import flash.events.Event;
	import flash.ui.MouseCursor;
	import flash.events.KeyboardEvent;
	/*Class DialoqueBox
		Fenetre Module de la fenetre de Dialogue
	*/
	public class Reponse extends MovieClip{
		public var fenetre:Fenetre;
		public var buffX:Number;
		public var buffY:Number;
		public var reduit:Boolean=false; // indique si la fenetre est déuite ou non 
		public var buffS:String="";  // buff le String lors de la réduction de fenetre
			
		public function Reponse(f:Fenetre,xx:int,yy:int){
			this.fenetre=f;
			fond.tf.multiline=true;//Plusieurs ligne dans la fenetre
			//fond.tf.wordWrap = true;//Met les mots trop long a la ligne
			this.x=xx;
			this.y=yy;
			fond.IPT.maxChars=120; // Nombre Maximum de characteres autorisé
			//fond.mov.titre.text = titre;
		}
		
		public function afficherThis(){
			fond.send.addEventListener(MouseEvent.CLICK , sendT); //Click sur le bouton d'envois de message
			//fond.mov.addEventListener(MouseEvent.MOUSE_MOVE , movThis); //Déplacement de la fenetre
			fond.IPT.addEventListener(KeyboardEvent.KEY_DOWN, sendTT); //Touche Entre pour envoyer message
			fenetre.addChild(this);
		}
		
		public function effacerThis(){
			fenetre.removeChild(this);
			fond.send.removeEventListener(MouseEvent.CLICK , sendT);
			//fond.mov.removeEventListener(MouseEvent.MOUSE_MOVE , movThis);
			fond.IPT.removeEventListener(KeyboardEvent.KEY_DOWN, sendTT); //Touche Entre pour envoyer message
		}
		
		//Ecris le String dans la fenetre
		public function addT(s:String){
			if(reduit){
				buffS += s +"\n";
			}else{
				fond.tf.text =fond.tf.text+ s+"\n";
				fond.tf.scrollV=fond.tf.maxScrollV; // On scroll la fenetre au dernier message envoyer
			}
		}
		
		//Envois au serveur le message du client
		public function sendT(evt:MouseEvent){
			//Si le client n'est pas desinateur
			if(!fenetre.currentUser.getDes() && !fenetre.currentUser.getSpec()){
				if (fond.IPT.text != ""){
					InterfaceSock.motClient(fond.IPT.text);
					fond.IPT.text = "";
				}
			}else{
				errDess();
			}
			
		}
		
		//Envois au serveur le message du client
		public function sendTT(evt:KeyboardEvent){
			//Si le client n'est pas desinateur
			if (fond.IPT.text != "" && evt.keyCode == 13){
				if(!fenetre.currentUser.getDes() && !fenetre.currentUser.getSpec()){
					InterfaceSock.motClient(fond.IPT.text);
					fond.IPT.text = "";
				}
				else{
				errDess();
				}
			}
		}
		
		//Deplacement graphique de la fenetre
		public function movThis(evt:MouseEvent){
			if(evt.buttonDown){
				fenetre.addChild(this);
				this.fond.x = evt.stageX - this.x - fond.mov.width/2 ;
				this.fond.y = evt.stageY - this.y - fond.mov.height/2 -5 ;
			}

		}
		
		//Reduction agrandissement de la fenetre
		public function reduirAgrandir(evt:MouseEvent){
			if(reduit){
				fond.gotoAndPlay(1);
				fond.IPT.addEventListener(KeyboardEvent.KEY_DOWN, sendTT);
				fond.tf.text=buffS;
				fond.IPT.maxChars=120;
				fond.tf.scrollV=fond.tf.maxScrollV;
				reduit=false;
			}else{
				buffS=fond.tf.text;
				fond.gotoAndPlay(2);
				reduit=true;
			}
		}
		
		//Si le dessinateur essait d'envoyer la réponse
		public function errDess():void{
			fond.IPT.text = "";
			addT("***** Erreur , vous n'avez pas le droit d'écrire ici !");
		}


	}
	
}