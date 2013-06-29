(ns s-exploration.core
  (:use [webfui.framework :only [launch-app]]
        [webfui.utilities :only [get-attribute]]
        [cljs.reader      :only [read-string]])
  (:require [s-exploration.renderers :as renderers])
  (:use-macros [webfui.framework.macros :only [add-dom-watch add-mouse-watch]]))


(defn c-log [x]
  (.log js/console (clj->js x))
  x)


(defn render-all [{:keys [sexps code]}]
  [:div
   [:header [:h1 "S-Exploration"]
    [:i "Graphical S-expression Editor"]]
   [:div#content
    [:div.grid-third
     [:textarea#code {:watch :code-watch
                      :value (str code)}
      (str code)]]
    [:div.grid-two-thirds
     [:div.visual-sexp
      (map renderers/render-sexp sexps)]
     [:span "Mouseover the blocks to see hints."]]]])


(add-dom-watch :code-watch [state new-el]
               (let [code (-> new-el second :value)]
                 (try
                   {:sexps (read-string (str "[" code "]"))
                    :code code}
                   (catch js/Object e
                     (c-log e)
                     {:code code}))))


(def sample-code "
(defn map [f [x & xs]]
  (cons (f x) (map f xs)))

(defn fact [n]
  (if (= n 1)
    1
    (* n (fact (- n 1)))))

(map fact [1 2 3 4 5])

{ :name \"Clojure\"
 :characteristics
 [\"simple\" \"awesome\"]
 :identity (fn [x] x) }
")


(launch-app (atom {:sexps (read-string (str "[" sample-code "]"))
                   :code  sample-code})
            render-all)
