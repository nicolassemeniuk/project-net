package net.project.model
{
	[Bindable]
	public class LoginModel
	{
		public function LoginModel(){
		}
		
		private static var modelLocator : LoginModel;
		

		public static function getInstance() : LoginModel 
	      {
	      	if ( modelLocator == null )
	      	{
	      		modelLocator = new LoginModel();
	      	}
	      		
	      	return modelLocator;
	      }
      
		public var username:String;
		
		public var password:String;
		
		public var userId:Number;
		
	}
}