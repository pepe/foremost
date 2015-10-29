(ns foremost.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [foremost.handlers]
              [foremost.subs]
              [foremost.routes :as routes]
              [foremost.views :as views]))

(devtools/set-pref! :install-sanity-hints true)
(devtools/install!)

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
