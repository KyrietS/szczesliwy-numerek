package eu.legnica.iilo.numerki

data class ApiResponse (
    var days: List<Day>
) {
    data class Day (
        var date: String,
        var numbers: List<Int>
    )
}