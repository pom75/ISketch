package  {	import flash.display.MovieClip;	/*Class User	*/	public class User extends MovieClip{
		//Pseudo du joueur		private var pseudo:String;
		//Points du joueur
		private var points:int = 0;
		//Es ce que user est un dessinateur ?
		private var dessinateur:Boolean = false;
		//Es ce que user est un spectateur ?
		private var isSpec:Boolean = false;				public function User(pseudo:String) {			this.pseudo=pseudo;		}
		
		public function getPseudo():String {
			return pseudo;
		}
		
		public function getPoints():int {
			return points;
		}
		
		public function setPoints(points:int):void {
			this.points = points;
		}
		
		public function setDes():void{
			this.dessinateur = true;
		}
		
		public function setNDes():void{
			this.dessinateur = false;
		}
		
		public function getDes():Boolean{
			return this.dessinateur;
		}
		
		public function setSpec(bool:Boolean):void{
			isSpec = bool;
		}
		
		public function getSpec():Boolean{
			return isSpec;
		}	}	}