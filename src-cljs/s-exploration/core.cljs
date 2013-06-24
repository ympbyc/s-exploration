(ns s-exploration.core
  (:use [webfui.framework :only [launch-app]]
        [webfui.utilities :only [get-attribute]]
        [cljs.reader      :only [read-string]])
  (:use-macros [webfui.framework.macros :only [add-dom-watch add-mouse-watch]]
               [clojure.core.match.js :only [match]]))


(defn c-log [x]
  (.log js/console (clj->js x))
  x)

(declare render-sexp)


(defn render-sym [sym]
  [:span.sexp-symbol.sexp-item (str sym)])


(defn render-defn [[_ name args & body]] ;TODO - support docstring
  [:div.wrap-defn.sexp-wrap
   [:span.sexp-defn.sexp-item "defn"]
   [:span.sexp-fn.sexp-item (str name)]
   [:div.wrap-vec.sexp-wrap (map render-sexp args)]
   [:div.newline
    (map render-sexp body)]])


(defn render-if [[_ cond then else]]
  [:div.wrap-if.sexp-wrap
   [:span.sexp-if "if"]
   (render-sexp cond)
   [:div.newline
    (render-sexp then)
    [:div.newline
     (render-sexp else)]]])


(defn render-call [[form & rest :as sexp]]
  (match form
    'defn (render-defn sexp)

    'if   (render-if sexp)

    _ [:div.wrap-ap.sexp-wrap
       [:span.sexp-fn.sexp-item (str form)]
       (map render-sexp rest)]))


(defn render-val [sexp]
  (cond
   (number? sexp)
   [:span.sexp-num.sexp-item (str sexp)]

   (string? sexp)
   [:span.sexp-str.sexp-item (str sexp)]

   (= sexp '&)
   [:span "&"]

   (symbol? sexp)
   [:span.sexp-symbol.sexp-item (str sexp)]

   true
   [:span.sexp-val.sexp-item (str sexp)]))


(defn render-sexp [sexp]
  (cond
   (vector? sexp)
   [:div.wrap-vec.sexp-wrap (map render-sexp sexp)]

   (coll? sexp)
   (render-call sexp)

   true
   (render-val sexp)))



(defn render-all [{:keys [sexps code]}]
  [:dic#content
   [:h1 "S式"]
   [:div
    [:textarea#code {:watch :code-watch}
     (str code)]]
   [:div.visual-sexp
    (map render-sexp sexps)]])



(add-dom-watch :code-watch [state new-el]
               (let [code (-> new-el second :value)]
                 (try
                   {:sexps (read-string (str "[" code "]"))
                    :code code}
                   (catch js/Object e
                     {:code code}))))


(def sample-code "
(defn map [f [x & xs]]
  (cons (f x (map f xs))))

(defn fact [n]
  (if (= n 1)
    1
    (* n (fact (- n 1)))))

(map fact [1 2 3 4 5])
")

(launch-app (atom {:sexps (read-string (str "[" sample-code "]"))
                   :code  sample-code})
            render-all)
