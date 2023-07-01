// Wait for the document to load
    document.addEventListener('DOMContentLoaded', function() {
    // Get a reference to the "Update" button
    var updateButton = document.getElementById('updateButton');

    // Add a click event listener to the "Update" button
    updateButton.addEventListener('click', function() {
    // Open the modal by targeting its ID and calling the 'modal' method
    $('#updateModal').modal('show');
});
});