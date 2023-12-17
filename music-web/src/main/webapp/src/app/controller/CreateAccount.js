'use strict'

angular.module('music').controller('CreateAccount', function($rootScope,$scope,$state,$dialog,User,Playlist,NamedPlaylist,Websocket,Restangular){
	$scope.createAccount = function(){
		var promise = Restangular.one('user/create-account').put($scope.user) 
		promise.then(function(){
			alert('user created')
			$state.transitionTo('login');
		})
	}
});