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
   [:h2 "Fun & Play"]
   [:h1 "&"]
   [:h2 "Ask Me Anything"]])

(def dragon-slide
 [:section
   [:h2 "Here may be"]
   [:h1 "Dragons"]
   [:h2 "Beware!"]])

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
     [:h1
      {:style
       {:text-decoration "line-through"}}
      "Frameworks"]
     [:ul
      [:li "Ember.js"]
      [:li "Angular.js"]
      [:li "React.js"]]]
    [:section
     [:h1 "Middleman"]
     [:h2 "On popular demand"]]
    ama-slide]
   :thursday
   [:div#slides
    [:section
     [:h1 "Thank you"]
     [:h2 "For your input and patience"]]
    [:section
     [:h1 "Some more internets"]
     [:h2 "Medium"]]
    [:section
     [:h1 "Some designing"]
     [:h2 "Pencil first. Pixel later."]
     [:h2 "Sketch"]]
    [:section
     [:h1 "Middleman publishing"]
     [:h2 "GitHub pages"]
     [:h2 "Static server"]]
    [:section
     [:h1 "More organization"]
     [:h2 "Help on project"]
     [:h2 "Wild ninja live coding"]]
     ama-slide
     dragon-slide]
   :friday
   [:div#slides
    [:section
     [:h1 "Maybe the last internets"]
     [:h2 "Squarespace"]]
    [:section
     [:h1 "What "]
     [:h1 "I actually did?"]]
    [:section
     [:h1 "Live coding"]
     [:h2 "Ninja style"]]
    [:section
     [:h1 "It is here"]
     [:h1 "have no fear"]]
     ama-slide
     dragon-slide
     dragon-slide]})

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
     [:h3 "Last week of October 2015, Czech University of Life Sciences"]
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
