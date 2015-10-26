(ns foremost.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame :refer [register-sub]]))

(register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))


(register-sub
 :current-slide
  (fn [db _]
    (reaction (:current-slide @db))))

(register-sub
 :slides-count
  (fn [db _]
    (reaction (:slides-count @db))))

(register-sub
 :active-day
  (fn [db _]
    (reaction (:active-day @db))))
