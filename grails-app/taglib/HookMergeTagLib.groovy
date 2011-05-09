class HookMergeTagLib {

  def custom = { attrs, body ->
     out << body()
  }
}
