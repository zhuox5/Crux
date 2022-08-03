package crux;

final class Authors {
  // TODO: Add author information.
  static final Author[] all = {new Author("Zhuo Xiao", "71208806", "zhuox5")};
}


final class Author {
  final String name;
  final String studentId;
  final String uciNetId;

  Author(String name, String studentId, String uciNetId) {
    this.name = name;
    this.studentId = studentId;
    this.uciNetId = uciNetId;
  }
}
