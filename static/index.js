angular.module('app', []).controller('indexController', function ($scope, $http) {
    const contextPath = 'http://localhost:8189/shop/api/v1';

    $scope.fillTable = function () {
        $http.get(contextPath + '/items')
            .then(function (response) {
                $scope.ProductsList = response.data;
            });
    };

    $scope.submitCreateNewProduct = function () {
        $http.post(contextPath + '/items', $scope.newProduct)
            .then(function (response) {
                $scope.fillTable();
            });
    };

    $scope.deleteProductById = function(productId) {
        $http({
            url: contextPath + '/items/' + productId,
            method: "DELETE"
        }).then(function (response) {
            $scope.fillTable();
        });
    }

    $scope.getProductById = function () {
      $http.get(contextPath + '/items/' + $scope.product.id)
        .then(function (response) {
            $scope.ProductsList = [response.data];
               }, function () {
                  $scope.ProductsList = [];
                  alert("Товар с таким ID не найден");
               });
    }

    $scope.updateProduct = function (product) {
      $http.put(contextPath + '/items', product)
        .then(function () {
          $scope.fillTable();
        }, function (err) {
          alert("Не удалось обновить товар: " + (err.data || err.status));
        });
    };
$scope.fillTable();
});