package net.project.model{
	
	[Bindable]
	public class States	{
		
		public function States(){
		}
		
		private static var instance:net.project.model.States;
		
		public static function getInstance():net.project.model.States {
			if (instance == null) {
				instance = new net.project.model.States();
			}
			return instance;
		}
		
		public var state:String;

	}
}