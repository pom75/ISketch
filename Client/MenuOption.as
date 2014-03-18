package  {
	
	import flash.display.MovieClip;
	import flash.events.MouseEvent;
	

	public class MenuOption extends MovieClip {
		public var fenetre:Fenetre;
		public var mess:MessageBox;
		
		public function MenuOption(fenetre:Fenetre) {
			this.fenetre = fenetre;
			this.x = 770;
			this.y =  50;
		}
		
		public function afficherThis():void{
			fenetre.addChild(this);
			help.addEventListener(MouseEvent.CLICK,aide);
			report.addEventListener(MouseEvent.CLICK,abuse);
			exit.addEventListener(MouseEvent.CLICK,quit);
			croix.addEventListener(MouseEvent.CLICK,fermer);
		}
		
		public function effacerThis():void{
			fenetre.removeChild(this);
			help.removeEventListener(MouseEvent.CLICK,aide);
			report.removeEventListener(MouseEvent.CLICK,abuse);
			exit.removeEventListener(MouseEvent.CLICK,quit);
			croix.removeEventListener(MouseEvent.CLICK,fermer);
		}
		
		public function aide(e:MouseEvent):void{
			mess = new MessageBox(fenetre,"Pour tout aide .....","Aide");
		}
		
		public function abuse(e:MouseEvent):void{
			if(!fenetre.currentUser.getDes() && !fenetre.currentUser.getSpec()){
				mess = new MessageBox(fenetre,"Attention tout abus .....","Report envoyé !");
				InterfaceSock.report();
			}
		}
		
		public function quit(e:MouseEvent):void{
			InterfaceSock.deconnexionGuest();
		}
		
		
		public function fermer(e:MouseEvent):void{
			effacerThis();
		}
		
		
		
	}
	
}
