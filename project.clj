(defproject foremost "0.2.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [binaryage/devtools "0.4.0"]
                 [reagent "0.5.1"]
                 [re-frame "0.4.1"]
                 [secretary "1.2.3"]
                 [garden "1.2.5"]]

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.3" :exclusions [cider/cider-nrepl]]
            [lein-garden "0.2.6"] ]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css/compiled"]

  :garden {:builds [{:id "screen"
                     :source-paths ["src/clj"]
                     :stylesheet foremost.css/screen
                     :compiler {:output-to "resources/public/css/compiled/screen.css"
                                :pretty-print? true}}]}

  :figwheel {:css-dirs ["resources/public/css/compiled"]}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]

                        :figwheel {:on-jsload "foremost.core/mount-root"}

                        :compiler {:main foremost.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs"]
                        :notify-command ["phantomjs" "test/unit-test.js" "test/unit-test.html"]
                        :compiler {:optimizations :whitespace
                                   :pretty-print true
                                   :output-to "test/js/app_test.js"
                                   :warnings {:single-segment-namespace false}}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main foremost.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]})
