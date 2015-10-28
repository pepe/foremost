(ns foremost.views
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan <! put!]]
            [clojure.string :refer [capitalize]]
            [goog.events :as events]
            [goog.events.EventType :as EventType]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]))

(defn- keypress-chan-events []
  (let [c (chan 1)]
    (events/listen js/window EventType/KEYPRESS #(put! c %))
    c))

(defonce listening false)

(defn- listen! []
  (when-not listening
    (set! listening true)
    (let [chan (keypress-chan-events)]
      (go-loop []
        (let [key (<! chan)
              code (-> key .-event_ .-keyCode)]
          (when (contains? #{108 13 32} code) (dispatch [:next-slide]))
          (when (= code 104) (dispatch [:previous-slide]))
          (recur))))))

;; --------------------
;; Slides
(def ama-slide
 [:section
   [:h2 "Fun & Play"]
   [:h1 "&"]
   [:h2 "Ask Me Anything"]])

(def slides
  {:monday
   [:div#slides
    [:section
     [:h2 "Contemporary"]
     [:h1 "Front-End"]
     [:h2 "Development"]]
    [:section
     [:h1 "Who am I?"]
     [:ul
      [:li
       "2"
       [:sup "nd"]
       " company in 15+ yrs"]
      [:li "Web and Typography"]
      [:li "@pepe on GitHub"]]]
    [:section
      [:h1 "Who are you?"]
      [:ul
       [:li "Name & Origin"]
       [:li "Engagement & Experience"]
       [:li "Groups"]]]
    [:section
     [:h1 "Organization"]
     [:ul
      [:li "From Day to Day"]
      [:li "Daily Tasks Triad (60%)"]
      [:li "Project (40%)"]]]
    [:section
     [:h1 "What's in Monday"]
     [:ul
      [:li "HTML - 5, Boilerplates, Generators"]
      [:li "CSS - 3, Preprocessors"]
      [:li "JS - ES7, Transpilers"]]]
    [:section
     [:header
      [:h1 "Start small"]
      [:h2 "Daily Tasks Triad"]]
     [:ul
      [:li "Create GitHub account"]
      [:li "Follow @pepe"]
      [:li "Star one related project"]]]
    ama-slide]
   :tuesday
   [:div#slides
    [:section
     [:h1 "More about @pepe"]
     [:h2 "damnpepe @ twitter / mixcloud"]
     [:h2 "Feel free to folow me."]]
   [:section
    [:h1 "More organization"]
    [:h2 "Design vs Code"]
    [:h2 "Work on Weekdays"]]
    [:section
     [:h1 "More tooling"]
     [:h2 "Atom editor"]]
    [:section
     [:h1 "Bootstrap"]
     [:h2 "and some more"]]
    [:section
     [:h1 "Frameworks"]
     [:ul
      [:li "Ember.js"]
      [:li "Angular.js"]
      [:li "React.js"]]]
    ama-slide
    [:section
     [:h1 "Here are dragons!"]]]})

;; --------------------
;; Components
(defn footer [with-slides]
  (let [slide (subscribe [:current-slide])
               slides-count (subscribe [:slides-count])
               active-day (subscribe [:active-day])]
    [:footer
     (for [day (keys slides)]
       ^{:key day}
       [:div
        [:a {:href (str "#/day/" (name day))} (capitalize (name day))]])
     (when with-slides [:div (str "(" (inc @slide) "/" @slides-count")")])]))

(defn day-panel []
  (listen!)
  (let [name (subscribe [:name])]
    (fn []
      (let [slide (subscribe [:current-slide])
            slides-count (subscribe [:slides-count])
            active-day (subscribe [:active-day])]
       [:div
        [:header
         [:span @name]
         [:strong "Josef Pospíšil"]
         [:nav
          [:button.prev
           {:on-click #(dispatch [:previous-slide])
            :disabled (when (= @slide 0) "disabled")}
           (char 8592)]
          [:input
           {:type "text"
            :placeholder (inc @slide)
            :value nil
            :on-change #(dispatch [:slide-changed (-> % .-target .-value)])}]
          [:button.next
           {:on-click #(dispatch [:next-slide])
            :disabled (when (= @slide (dec @slides-count)) "disabled")}
           (char 8594)]]]
        [:main
         {:style {:width (str @slides-count "00vw")
                  :transform (str "translateX(-" @slide "00vw)")}}
         (get slides @active-day)]
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
     [:h3 "Last week of October 2015, Czech University of Life Sciences"]
     [:div [:a {:href "#/"} "go to Presentation"]]]))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :day [] [day-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
