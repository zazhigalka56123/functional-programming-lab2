(defproject prefix-tree "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot prefix-tree.core
  :target-path "target/%s"
  :plugins [[lein-midje "3.2.2"]
            [lein-cljfmt "0.8.2"]
            [lein-kibit "0.1.8"]
            [lein-bikeshed "0.5.2"]]
  :profiles {:dev {:dependencies [[midje "1.9.9"]
                                  [org.clojure/test.check "1.1.1"]]}}
  :aliases {"lint" ["do"
                    ["cljfmt" "check"]
                    ["kibit"]
                    ["bikeshed" "--max-line-length" "120"]]})
