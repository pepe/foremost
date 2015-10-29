(ns foremost.css
  (:refer-clojure :exclude [+ - * / rem])
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [vw vh rem px]]
            [garden.color :refer [rgba]]))

(def grey (rgba 194 204 203 1))
(def red (rgba 230 0 111 1))
(def black (rgba 44 46 33 1))
(def white (rgba 255 255 255 1))
(def blue (rgba 81 83 61 1))

(defstyles screen
  [:html {:font-size (px 12)}]
  [:body {:font-family "Vegan Sans, sans-serif"
          :font-weight 800
          :background-color grey
          :color black
          :margin 0
          :overflow "hidden"}
   [:div#app
    {:width "100vw"
     :height "100vh"}]
   [:&>div>div>header
    {:position "fixed"
     :height (rem 4)
     :top (rem 1)
     :left (rem 1)
     :width "calc(100vw - 2rem)"
     :display "flex"
     :justify-content "space-between"
     :z-index 3
     :opacity 0}
    [:&:hover {:opacity 1}]
    [:nav
     {:display "flex"
      :height (rem 5)}
     [:button
      {:border "none"
       :border-radius (rem 2)
       :padding [[(rem 0.35) (rem 1.5) (rem 0.45)]]
       :margin [[(rem 0.5) (rem 0.5)]]
       :background blue
       :color white
       :opacity 0.5
       :font-size (rem 2)
       :font-weight 900}]
     [:input
      {:font-size (rem 2)
       :width (rem 2)
       :text-align "center"
       :background grey
       :border "none"}]]]
   [:main
    {:position "absolute"
     :transition-property "transform"
     :transition-duration "250ms"}
    [:&>div {:display "flex"}]
    [:section
     {:display "flex"
      :flex-direction "column"
      :justify-content "space-around"
      :align-items "flex-start"
      :width "100vw"
      :height "100vh"
      :text-align "center"}
     [:header :h1 :h2 :ul
      {:margin-left "auto"
       :margin-right "auto"}]
     [:h1 :h2 {:font-family "Hrot, sans-serif"}]
     [:h1
      {:font-size (rem 8)
       :font-weight 200
       :color red}]
     [:h2
      {:font-size (rem 4)
       :font-weight 500
       :color blue}]
     [:ul {:margin [[(rem 2) "auto"]]
           :font-size (rem 6)
           :text-align "left"
           :list-style "none"}
      [:li {:margin-bottom (rem 3)}
       [:sup {:font-size (rem 2)}]]]]]
   [:footer
    {:display "flex"
     :position "fixed"
     :bottom (rem 1)
     :right (rem 1)
     :width "30vw"
     :justify-content "space-between"}]]
)
