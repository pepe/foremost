(ns foremost.handlers
    (:require [re-frame.core :as re-frame :refer [subscribe register-handler path]]
              [foremost.db :as db]))

(register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(register-handler
 :next-slide
  (path :current-slide)
  (fn [slide]
    (let [slides-count (subscribe [:slides-count])]
      (if (> @slides-count (inc slide) )
        (inc slide)
        (slide)))))

(register-handler
 :previous-slide
  (path :current-slide)
  (fn [slide]
    (if (> (dec slide) 0)
      (dec slide)
      0)))

(register-handler
 :slide-changed
  (path :current-slide)
  (fn [slide [_ new-slide]]
    (let [slides-count (subscribe [:slides-count])]
      (.log js/console new-slide @slides-count)
      (cond
        (> new-slide @slides-count) (dec @slides-count)
        (< new-slide 1) 1
        :else (dec new-slide)))))

