(ns foremost.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]))

;; --------------------
(defn home-panel []
  (let [name (subscribe [:name])]
    (fn []
      (let [slide (subscribe [:current-slide])
            slides-count (subscribe [:slides-count])]
       [:div
        [:header @name
         [:nav
          [:button.prev
           {:on-click #(dispatch [:previous-slide])
            :disabled (when (= @slide 0) "disabled")}
           "<"]
          [:input
           {:type "text"
            :placeholder (inc @slide)
            :value nil
            :on-change #(dispatch [:slide-changed (-> % .-target .-value)])}]
          [:button.next
           {:on-click #(dispatch [:next-slide])
            :disabled (when (= @slide (dec @slides-count)) "disabled")}
           ">"]]]
        [:main
         {:style {:width (str @slides-count "00vw")
                  :transform (str "translateX(-" @slide "00vw)")}}
         [:section
          [:h2 "Contemporary"]
          [:h1 "Front-End"]
          [:h2 "Development"]]
         [:section
          [:h1 "Who am I?"]
          [:ul
           [:li "~20 years in the industry"]
           [:li "2nd company and counting"]
           [:li "Ruby, JavaScript, ClojureScript"]
           [:li "Modern web and typography"]
           [:li "@pepe on GitHub"]]]
         [:section
           [:h1 "Who are you?"]
           [:ul
            [:li "Name&Origin"]
            [:li "Level of engagement"]
            [:li "Past experience"]
            [:li "Group"]]]
         [:section
          [:h1 "What's in this?"]
          [:ul
           [:li "Modern HTML"]
           [:li "Modern CSS"]
           [:li "Modern JS"]]]
         [:section
          [:header
           [:h1 "But start small"]
           [:h2 "Daily task's triad"]]
          [:ul
           [:li "Create GitHub account"]
           [:li "Follow @pepe"]
           [:li "Star one related project"]]]]
        [:footer (str "(" (inc @slide) "/" @slides-count")")]]))))

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
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
