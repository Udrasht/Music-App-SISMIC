'use strict';

/**
 * Playlist controller.
 */
angular.module('music').controller('Playlist', function($scope, $state, $stateParams, Restangular, Playlist, NamedPlaylist) {
  // Load playlist
  Restangular.one('playlist', $stateParams.id).get().then(function(data) {
    $scope.playlist = data;
  });
  
  
  $scope.getRandomIndexForRecommendation = function(max){
	  console.log(max+" songs")
	  let result = []
	  for (let i=0 ;i<Math.min(max,5);i++){
		  let idx = Math.floor(Math.random() * (max - 0 + 1)) + 0;
		  result.push(i);
	  }
	  return result;
  }
  
  $scope.getArtistsForLastFmRecommendation = function(arr){
	  var result="";
	  for (let i=0;i<arr.length;i++){
		  result = result+ $scope.playlist.tracks[arr[i]].artist.name+","
	  }
	  return result;
  }
  
   $scope.getTracksForLastFmRecommendation = function(arr){
	  var result="";
	  for (let i=0;i<arr.length;i++){
		  result = result+ $scope.playlist.tracks[arr[i]].title+","
	  }
	  return result;
  }
   
  $scope.lastfmRecommend = function(){
	console.log("lastfm recommendation")
	console.log($scope.playlist.tracks)
	let arrIdx = $scope.getRandomIndexForRecommendation($scope.playlist.tracks.length)
	console.log(arrIdx);
	
	let allTracks=$scope.getArtistsForLastFmRecommendation(arrIdx).slice(0,-1);
	let allArtists=$scope.getTracksForLastFmRecommendation(arrIdx).slice(0,-1)
	

	console.log("ALL TRACKS "+ allTracks)
	console.log("ALL ARTISTS "+allArtists)
	
	allArtists = allArtists.replace(/\s+/g, "+");
	allTracks= allTracks.replace(/\s+/g, "+");
	var queryParams = {thirdPartyType:'LASTFM',queryString:allArtists, queryType:allTracks}
	console.log(queryParams)
	
	Restangular.one('search/recommend-third-party').get(queryParams).then(function(data){
		console.log("data fetched from lastfm recommendation api");
		$scope.recommendedTracks = data
		console.log(data)
	},function(error){
		console.log("error fetching from last fm recommendation api")
		console.log(error)
	})
	
  }
  
  
   $scope.spotifyRecommend = function(){
	console.log("spotify recommendation")
	let arrIdx = $scope.getRandomIndexForRecommendation($scope.playlist.tracks.length)
	console.log(arrIdx);
	let allArtists=$scope.getArtistsForLastFmRecommendation(arrIdx).slice(0,-1)
	console.log(allArtists)
	allArtists= allArtists.replace(/\s+/g, "+");
	var queryParams = {thirdPartyType:'SPOTIFY',queryString:allArtists, queryType:'seed_artists'}
	console.log(queryParams)
	
	Restangular.one('search/recommend-third-party').get(queryParams).then(function(data){
		console.log("data fetched from spotify recommendation api");
		$scope.recommendedTracks = data
	},function(error){
		console.log("error fetching from spotify recommendation api")
		console.log(error)
	})
	
  }

  // Play a single track
  $scope.playTrack = function(track) {
    Playlist.removeAndPlay(track);
  };

  // Add a single track to the playlist
  $scope.addTrack = function(track) {
    Playlist.add(track, false);
  };

  // Add all tracks to the playlist in a random order
  $scope.shuffleAllTracks = function() {
    Playlist.addAll(_.shuffle(_.pluck($scope.playlist.tracks, 'id')), false);
  };

  // Play all tracks
  $scope.playAllTracks = function() {
    Playlist.removeAndPlayAll(_.pluck($scope.playlist.tracks, 'id'));
  };

  // Add all tracks to the playlist
  $scope.addAllTracks = function() {
    Playlist.addAll(_.pluck($scope.playlist.tracks, 'id'), false);
  };

  // Like/unlike a track
  $scope.toggleLikeTrack = function(track) {
    Playlist.likeById(track.id, !track.liked);
  };

  // Remove a track
  $scope.removeTrack = function(order) {
    NamedPlaylist.removeTrack($scope.playlist, order).then(function(data) {
      $scope.playlist = data;
    });
  };

  // Delete the playlist
  $scope.remove = function() {
    NamedPlaylist.remove($scope.playlist).then(function() {
      $state.go('main.default');
    });
  };

  // Update UI on track liked
  $scope.$on('track.liked', function(e, trackId, liked) {
    var track = _.findWhere($scope.playlist.tracks, { id: trackId });
    if (track) {
      track.liked = liked;
    }
  });

  // Configuration for track sorting
  $scope.trackSortableOptions = {
    forceHelperSize: true,
    forcePlaceholderSize: true,
    tolerance: 'pointer',
    handle: '.handle',
    containment: 'parent',
    helper: function(e, ui) {
      ui.children().each(function() {
        $(this).width($(this).width());
      });
      return ui;
    },
    stop: function (e, ui) {
      // Send new positions to server
      $scope.$apply(function () {
        NamedPlaylist.moveTrack($scope.playlist, ui.item.attr('data-order'), ui.item.index());
      });
    }
  };
});