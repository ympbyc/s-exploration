(ns s-exploration.renderers
  (:use-macros [clojure.core.match.js :only [match]]))

(declare render-sexp)


(defn render-sym [sym]
  [:span.sexp-symbol.sexp-item (str sym)])


(defn render-vector [sexp]
  [:div.wrap-vec.sexp-wrap {:title "a vector"}
   (map render-sexp sexp)])


(defn render-table [hmap]
  (map
   (fn [[k v]]
     [:tr
      [:td.wrap-mapkey (render-sexp k)]
      [:td.wrap-mapval (render-sexp v)]])
   hmap))


(defn render-map [hmap]
  [:div.wrap-map.sexp-wrap {:title "a map"}
   [:table (render-table hmap)]])


(defn render-defn [[_ name args & body]]
  "TODO - support docstring"
  [:div.wrap-defn.sexp-wrap {:draggable "true" :title "function definition"}
   [:span.sexp-defn.sexp-item "defn"]
   [:span.sexp-defn-fname.sexp-item {:title "function name"}
    (str name)]
   [:div.wrap-vec.sexp-wrap {:title "function parameters"}
    (map render-sexp args)]
   [:div.newline {:title "function body"}
    (map render-sexp body)]])


(defn render-fn [[_ args & body]]
  [:div.wrap-fn.sexp-wrap {:title "a lambda"}
   [:span.sexp-item "fn"]
   [:div.wrap-vec.sexp-wrap {:title "function parameters"}
    (map render-sexp args)]
   [:div.inline-block {:title "function body"}
    (map render-sexp body)]])


(defn render-if [[_ cond then else]]
  [:div.wrap-if.sexp-wrap {:title "a specialform - if"}
   [:span.sexp-if "if"]
   [:div.inline-block {:title "condition"}
    (render-sexp cond)]
   [:div.newline
    [:div.inline-block {:title "then branch"}
     (render-sexp then)]
    [:div.newline
     [:div.inline-block {:title "else branch"}
      (render-sexp else)]]]])


(defn render-call [[form & rest :as sexp]]
  (match form
         'defn (render-defn sexp)

         'fn   (render-fn sexp)

         'if   (render-if sexp)

         _ [:div.wrap-ap.sexp-wrap {:title "function application"}
            [:span.sexp-fn.sexp-item {:title "name of the function"} (str form)]
            [:div.inline-block {:title "arguments to the function"}
             (map render-sexp rest)]]))



(defn render-val [sexp]
  (cond
   (number? sexp)
   [:span.sexp-num.sexp-item {:title "number"} (str sexp)]

   (string? sexp)
   [:span.sexp-str.sexp-item {:title "string"} (str sexp)]

   (= sexp '&)
   [:span {:title "ampersand followed by a parameter denotes rest-argument"}
    "&"]

   (symbol? sexp)
   [:span.sexp-symbol.sexp-item {:title "symbol"} (str sexp)]

   (keyword? sexp)
   [:span.sexp-val.sexp-item {:title "keyword"} (str sexp)]

   true
   [:span.sexp-val.sexp-item {:title "value"} (str sexp)]))



(defn render-sexp [sexp]
  (cond
   (vector? sexp)
   (render-vector sexp)

   (map? sexp)
   (render-map sexp)


   (coll? sexp)
   (render-call sexp)

   true
   (render-val sexp)))
