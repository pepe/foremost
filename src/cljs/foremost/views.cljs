(ns foremost.views
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan <! put!]]
            [clojure.string :refer [capitalize]]
            [goog.events :as events]
            [goog.events.EventType :as EventType]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]))

(defn- keypress-chan-events []
  (let [c (chan 1)]
    (events/listen js/window EventType/KEYDOWN #(put! c %))
    c))

(defonce listening false)

(defn- listen! []
  (when-not listening
    (set! listening true)
    (let [chan (keypress-chan-events)]
      (go-loop []
        (let [key (<! chan)
              code (-> key .-event_ .-keyCode)]
          (when (contains? #{108 13 32 39 40} code) (dispatch [:next-slide]))
          (when (contains? #{104 37 38} code ) (dispatch [:previous-slide]))
          (recur))))))

;; --------------------
;; Slides
(def ama-slide
 [:section
   [:h2 "Hra & Zábava"]
   [:h1 "&"]
   [:h2 "Ptejte se na cokoliv"]])

(def dragon-slide
 [:section
   [:h2 "Tady mohou být"]
   [:h1 "Draci"]
   [:h2 "Opatrně!"]])

(def slides
  {:lambda
   [:div#slides
    [:section
     [:h2 "Podještědská Lambda"]
     [:h1 "vs"]
     [:h2 "re-frame"]]
    [:section
     [:h1 "Kdo jsem?"]
     [:ul
      [:li "První Rubysta v ČR"]
      [:li "Web a Typografie"]
      [:li "pepe @ GitHub"]]]
    [:section
     [:h1 "Co se bude ukazovat?"]
     [:ul
      [:li "cljs env"]
      [:li "Reagent"]
      [:li "Re-frame"]]]
    ama-slide
    dragon-slide]})

;; --------------------
;; Components
(defn footer [with-slides]
  (let [slide (subscribe [:current-slide])
               slides-count (subscribe [:slides-count])]
    [:footer
     (when with-slides [:div (str "(" (inc @slide) "/" @slides-count")")])]))

(defn slides-panel []
  (listen!)
  (let [name (subscribe [:name])]
    (fn []
      (let [slide (subscribe [:current-slide])
            slides-count (subscribe [:slides-count])
            active-slides (subscribe [:active-slides])
            prev-arrow (char 8592)
            next-arrow (char 8594)]
        [:div
         [:header
          [:span @name]
          [:strong "Josef Pospíšil"]
          [:nav
           [:button.prev
            {:on-click #(dispatch [:previous-slide])
             :disabled (when (= @slide 0) "disabled")}
            prev-arrow
            ]
           [:input
            {:type "text"
             :placeholder (inc @slide)
             :value nil
             :on-change #(dispatch [:slide-changed (-> % .-target .-value)])}]
           [:button.next
            {:on-click #(dispatch [:next-slide])
             :disabled (when (= @slide (dec @slides-count)) "disabled")}
            next-arrow]]]
         [:main
          {:style {:width (str @slides-count "00vw")
                   :transform (str "translateX(-" @slide "00vw)")}}
          (get slides @active-slides)]
         [footer true]]))))

(defn home-panel []
  (let [name (subscribe [:name])]
    [:div
      [:header
       [:span @name]
       [:strong "Josef Pospíšil"]]
      [:main
       [:section
        [:h2 "Contemporary"]
        [:h1 "Front-End"]
        [:h2 "Development"]]]
      [footer false]]))

(defn about-panel []
  (fn []
    [:div
     [:h1 "Contemporary Front End Development"]
     [:h2 "Presented by Josef \"pepe\" Pospíšil"]
     [:h3 "10. prosince 2015, Podještedská Lambda"]
     [:div [:a {:href "#/"} "go to Presentation"]]]))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :slides [] [slides-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
