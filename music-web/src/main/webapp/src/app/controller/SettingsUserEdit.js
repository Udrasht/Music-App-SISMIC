'use strict';

/**
 * Settings user edition page controller.
 */
angular.module('music').controller('SettingsUserEdit', function($scope, $dialog, $state, $stateParams, Restangular) {
  /**
   * Returns true if in edit mode (false in add mode).
   */
  $scope.isEdit = function() {
    return $stateParams.username;
  };
  
  /**
   * In edit mode, load the current user.
   */
  if ($scope.isEdit()) {
    Restangular.one('user', $stateParams.username).get().then(function(data) {
      $scope.user = data;
    });
  }

  /**
   * Update the current user.
   */
  $scope.edit = function() {
    var promise = null;
    
    if ($scope.isEdit()) {
      promise = Restangular
      .one('user', $stateParams.username)
      .post('', $scope.user);
    } else {
      promise = Restangular
      .one('user')
      .put($scope.user);
    }
    
    promise.then(function() {
      $scope.loadUsers();
      $state.transitionTo('main.settingsuser');
    });
  };

  /**
   * Delete the current user.
   */
  $scope.remove = function () {
    var title = 'Delete user';
    var msg = 'Do you really want to delete this user?';
    var btns = [{result:'cancel', label: 'Cancel'}, {result:'ok', label: 'OK', cssClass: 'btn-primary'}];

    $dialog.messageBox(title, msg, btns, function(result) {
      if (result == 'ok') {
        Restangular.one('user', $stateParams.username).remove().then(function() {
          $scope.loadUsers();
          $state.transitionTo('main.settingsuser');
        }, function () {
          $state.transitionTo('main.settingsuser');
        });
      }
    });
  };

});