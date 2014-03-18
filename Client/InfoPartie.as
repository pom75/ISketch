package  {
	
	import flash.display.MovieClip;
	import flash.utils.Timer;
	import flash.events.TimerEvent;
	import flash.events.MouseEvent;
	
	
	public class InfoPartie extends MovieClip {
		public var fenetre:Fenetre;
		private var start:Boolean = false;
		private var currentD:String = "Personne";
		private var prochainD:String = "Personne";
		private var motD:String = "";
		//timer
		private var minutes:uint = 3;
		private var secondes:uint = 0;
		private var total:uint = minutes*60 + secondes;
		private var myTimer:Timer = new Timer(1000, total);
		
		public function InfoPartie(f:Fenetre,xx:int,yy:int) {
			this.fenetre = f;
			this.x=xx;
			this.y=yy;
			this.cacheMot();
			waitOther();
			this.pass.visible = false;
		}
		
		public function startT():void{
			this.vain.visible=false;
			minutes = 3;
			secondes = 0;
			this.total = minutes*60 + secondes;
			myTimer.reset();
			myTimer.start();
			myTimer.addEventListener("timer", InitTimer);
			this.start=true;
		}
		
		public function waitOther():void{
			this.vain.text = "En attente d'autres joueurs";
			this.vain.visible = true;
		}
		
		public function vainqueur(pseudo:String):void{
			this.vain.text = "Vainqueur round : "+pseudo;
			this.vain.visible = true;
		}
		
		public function stopT():void{
			this.start=false;
			timer_txt.text = "Fin du round";
			myTimer.stop();
		}
		
		public function changeT(m:uint,s:uint):void{
			myTimer.stop();
			myTimer.reset();
			myTimer.start();
			myTimer.addEventListener("timer", InitTimer);
			this.start=true;
			this.setMin(m);
			this.setSec(s);
		}
		
		public function setCurrentD(user:String):void{
			this.currentD = user;
			this.des.text = currentD;
		}
		
		public function setMin(m:uint):void{
			this.minutes= m;
			this.total = minutes*60 + secondes;;
		}
		
		public function setSec(s:uint):void{
			this.secondes= s;
			this.total = minutes*60 + secondes;
		}
		
		public function setMot(mot:String){
			this.motD = mot;
			this.mot.text = motD;
			this.mot.visible = true;
			this.text.visible = true;
			this.pass.visible = true;
		}
		
		public function cacheMot(){
			this.mot.visible = false;
			this.text.visible = false;
		}
		
		public function afficherThis(){
			fenetre.addChild(this);
			pass.addEventListener(MouseEvent.CLICK,passe);
		}
		
		public function effacerThis(){
			fenetre.removeChild(this);
			pass.removeEventListener(MouseEvent.CLICK,passe);
		}
		
		public function getCurrentD():String{
			return this.currentD;
		}
		
		public function passe(e:MouseEvent){
			InterfaceSock.pass();
		}
		public function InitTimer(ev:TimerEvent):void 
		{
			var timeLeft:uint = total-myTimer.currentCount
			var n:uint = (timeLeft%60);

			if (n < 10) 
			{
				timer_txt.text = uint((timeLeft/60))+" : 0"+n;
			} else {
				timer_txt.text = uint((timeLeft/60))+" : "+n;
			}
		}
		
		public function getStart():Boolean{
			return this.start;
		}
		
	}
	
}
