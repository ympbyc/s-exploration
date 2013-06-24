(ns s-exploration.renderers
  (:use-macros [clojure.core.match.js :only [match]]))

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
